package com.example.consumer.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * 带监控功能的线程池（统计活跃线程数、队列大小、拒绝任务数）
 */

public class MonitoredThreadPoolExecutor extends ThreadPoolExecutor {

    private final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(MonitoredThreadPoolExecutor.class);
    private final String poolName;
    private final Counter rejectedTaskCounter;
    private final Counter completedTaskCounter;
    private final Counter exceptionTaskCounter;

    // Redis模板（用于存储拒绝任务）
    private final StringRedisTemplate redisTemplate;

    /**
     * 内部类：带监控的RejectedExecutionHandler
     */
    private class InternalRejectedExecutionHandler implements RejectedExecutionHandler {

        private final RejectedExecutionHandler originalHandler;

        public InternalRejectedExecutionHandler(RejectedExecutionHandler originalHandler) {
            this.originalHandler = originalHandler;
        }

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            // 统计拒绝任务数
            rejectedTaskCounter.increment();
            log.warn("[{}] 任务被拒绝，当前队列大小：{}，活跃线程数：{}", poolName, getQueue().size(), getActiveCount());

            // 调用原始handler
            originalHandler.rejectedExecution(r, executor);
        }
    }

    /**
     * 构造方法
     */
    public MonitoredThreadPoolExecutor(
        int corePoolSize,
        int maximumPoolSize,
        long keepAliveTime,
        TimeUnit unit,
        BlockingQueue<Runnable> workQueue,
        ThreadFactory threadFactory,
        RejectedExecutionHandler handler,
        String poolName,
        MeterRegistry meterRegistry,
        StringRedisTemplate redisTemplate
    ) {
        // 先初始化父类，使用临时的RejectedExecutionHandler
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
        // 初始化实例变量
        this.poolName = poolName;
        this.redisTemplate = redisTemplate;

        // 初始化监控指标（Prometheus Counter）
        this.rejectedTaskCounter = Counter.builder("thread_pool_rejected_tasks_total")
            .tag("pool_name", poolName)
            .description("线程池拒绝任务总数")
            .register(meterRegistry);

        this.completedTaskCounter = Counter.builder("thread_pool_completed_tasks_total")
            .tag("pool_name", poolName)
            .description("线程池已完成任务总数")
            .register(meterRegistry);

        this.exceptionTaskCounter = Counter.builder("thread_pool_exception_tasks_total")
            .tag("pool_name", poolName)
            .description("线程池执行异常任务总数")
            .register(meterRegistry);

        // 替换为带监控的RejectedExecutionHandler
        super.setRejectedExecutionHandler(new InternalRejectedExecutionHandler(handler));
    }

    /**
     * 任务执行后回调（统计完成数、异常数）
     */
    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        completedTaskCounter.increment(); // 完成任务数+1
        if (t != null) {
            exceptionTaskCounter.increment(); // 异常任务数+1
            log.error("[{}] 任务执行异常", poolName, t);
        }
    }

    /**
     * 获取线程池当前监控指标
     */
    public Map<String, Number> getMonitorMetrics() {
        Map<String, Number> metrics = new HashMap<>();
        metrics.put("core_pool_size", getCorePoolSize());
        metrics.put("max_pool_size", getMaximumPoolSize());
        metrics.put("active_count", getActiveCount());
        metrics.put("queue_size", getQueue().size());
        metrics.put("queue_remaining_capacity", getQueue().remainingCapacity());
        metrics.put("completed_tasks", getCompletedTaskCount());
        metrics.put("rejected_tasks", rejectedTaskCounter.count());
        metrics.put("exception_tasks", exceptionTaskCounter.count());
        return metrics;
    }

    public String getPoolName() {
        return poolName;
    }

    public Counter getRejectedTaskCounter() {
        return rejectedTaskCounter;
    }

    public Counter getCompletedTaskCounter() {
        return completedTaskCounter;
    }

    public Counter getExceptionTaskCounter() {
        return exceptionTaskCounter;
    }
}
