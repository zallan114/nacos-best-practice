package com.example.consumer.config;

import com.alibaba.fastjson.JSON;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * 自定义任务拒绝策略：存入Redis，支持后续重试
 */

public class CustomRejectedExecutionHandler implements RejectedExecutionHandler {

    private final Logger log = LoggerFactory.getLogger(CustomRejectedExecutionHandler.class);
    private final StringRedisTemplate redisTemplate;
    private final String poolName;

    public CustomRejectedExecutionHandler(StringRedisTemplate redisTemplate, String poolName) {
        this.redisTemplate = redisTemplate;
        this.poolName = poolName;
    }

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        try {
            // 生成唯一任务ID（时间戳+随机数）
            String taskId = ThreadPoolConstant.REJECTED_TASK_KEY_PREFIX + System.currentTimeMillis() + "_" + Math.random();
            // 序列化任务（实际生产可使用更可靠的序列化方式，如ProtoBuf）
            String taskJson = JSON.toJSONString(r);
            // 存入Redis，过期时间24小时
            redisTemplate.opsForValue().set(taskId, taskJson, 24, TimeUnit.HOURS);
            log.info("[{}] 拒绝任务已存入Redis，taskId：{}", poolName, taskId);
        } catch (Exception e) {
            log.error("[{}] 拒绝任务存储Redis失败", poolName, e);
            // 兜底：调用者线程执行
            new ThreadPoolExecutor.CallerRunsPolicy().rejectedExecution(r, executor);
        }
    }
}
