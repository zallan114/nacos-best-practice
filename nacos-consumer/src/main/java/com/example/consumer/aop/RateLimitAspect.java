package com.example.consumer.aop;

import com.google.common.util.concurrent.RateLimiter;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseBody;

/**
    // 限制该接口每秒最多 5 个请求
    @RateLimit(qps = 5.0, message = "查询接口请求频繁，请1秒后重试")
    @GetMapping("/query")
    public String query() {
        return "查询成功";
    }
 */

@Aspect
@Component
public class RateLimitAspect {

    // 存储每个接口的 RateLimiter 实例（key：方法签名，value：令牌桶）
    private final Map<String, RateLimiter> rateLimiterMap = new HashMap<>();

    // 拦截所有标注 @RateLimit 的方法
    @Around("@annotation(com.example.demo.annotation.RateLimit)")
    @ResponseBody
    public Object limit(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RateLimit annotation = method.getAnnotation(RateLimit.class);

        // 获取当前方法的 RateLimiter（不存在则创建）
        String methodKey = method.getDeclaringClass().getName() + "." + method.getName();
        RateLimiter rateLimiter = rateLimiterMap.computeIfAbsent(methodKey, k -> RateLimiter.create(annotation.qps())); // 初始化令牌桶 QPS

        // 尝试获取令牌：acquire() 阻塞等待，tryAcquire() 非阻塞
        if (rateLimiter.tryAcquire()) {
            return joinPoint.proceed(); // 获取令牌成功，执行原方法
        } else {
            // 限流失败，返回提示信息（可自定义统一返回格式）
            Map<String, Object> result = new HashMap<>();
            result.put("code", 429);
            result.put("message", annotation.message());
            return result;
        }
    }
}
/**
 * 
<!-- Guava 核心依赖 -->
<dependency>
    <groupId>com.google.guava</groupId>
    <artifactId>guava</artifactId>
    <version>32.1.3-jre</version>
</dependency>
<!-- Spring AOP 用于切面限流 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-aop</artifactId>
</dependency>
 * 
 */
