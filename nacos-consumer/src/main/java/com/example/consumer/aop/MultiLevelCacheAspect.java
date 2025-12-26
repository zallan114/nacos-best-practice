package com.example.consumer.aop;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * 
    // 多级缓存：本地缓存5分钟，Redis缓存1小时
    @MultiLevelCache(name = "user", localExpire = 300, redisExpire = 3600)
    public User getUserById(Long id) {
        // 模拟数据库查询
        return userMapper.selectById(id);
    }
 * 
 */
@Aspect
@Component
public class MultiLevelCacheAspect {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // @Autowired
    // private BloomFilter<String> bloomFilter;

    // 本地缓存容器（代替CaffeineCacheManager，手动控制更灵活）
    private final Cache<String, Object> localCache = Caffeine.newBuilder().expireAfterWrite(5, TimeUnit.MINUTES).maximumSize(1000).build();

    // @Autowired
    // private Cache<String, Object> caffeineCacheManager;

    @Around("@annotation(com.example.consumer.aop.MultiLevelCache)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        MultiLevelCache annotation = method.getAnnotation(MultiLevelCache.class);
        String cacheKey = generateKey(joinPoint, signature);

        // 1. 先查本地缓存
        Object result = localCache.getIfPresent(cacheKey);
        if (result != null) {
            return result;
        }

        // 2. 本地缓存未命中，查Redis
        result = redisTemplate.opsForValue().get(cacheKey);
        if (result != null) {
            // 回写本地缓存
            localCache.put(cacheKey, result);
            return result;
        }

        // 3. 布隆过滤器判断key是否存在，防缓存穿透
        // if (!bloomFilter.mightContain(cacheKey)) {
        //     return null; // 不存在的key直接返回，不查数据库
        // }

        // 4. 加互斥锁，防缓存击穿（分布式锁）
        String lockKey = "lock:" + cacheKey;
        boolean locked = redisTemplate.opsForValue().setIfAbsent(lockKey, "1", 30, TimeUnit.SECONDS);
        if (!locked) {
            // 未抢到锁，重试查Redis（避免并发查库）
            Thread.sleep(50);
            return redisTemplate.opsForValue().get(cacheKey);
        }

        try {
            // 5. 缓存都未命中，查数据库
            result = joinPoint.proceed();
            // 6. 缓存空值，防缓存穿透
            if (result == null) {
                redisTemplate.opsForValue().set(cacheKey, "null", 60, TimeUnit.SECONDS);
                localCache.put(cacheKey, null);
                return null;
            }
            // 7. 写入Redis和本地缓存
            redisTemplate.opsForValue().set(cacheKey, result, annotation.redisExpire(), TimeUnit.SECONDS);
            localCache.put(cacheKey, result);
            return result;
        } finally {
            // 释放锁
            redisTemplate.delete(lockKey);
        }
    }

    // 生成缓存key
    private String generateKey(ProceedingJoinPoint joinPoint, MethodSignature signature) {
        // 实现和之前 KeyGenerator 一致的逻辑
        StringBuilder sb = new StringBuilder();
        sb.append(joinPoint.getTarget().getClass().getName()).append(":");
        sb.append(signature.getMethod().getName()).append(":");
        for (Object param : joinPoint.getArgs()) {
            sb.append(param.toString());
        }
        return sb.toString();
    }
}
