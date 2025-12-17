package com.example.consumer.aop;
import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface MultiLevelCache {
    String name() default ""; // 缓存名称
    long localExpire() default 300; // 本地缓存过期时间（秒）
    long redisExpire() default 3600; // Redis缓存过期时间（秒）
}
