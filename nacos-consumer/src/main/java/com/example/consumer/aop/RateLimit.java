package com.example.consumer.aop;

import java.lang.annotation.*;

// 限流注解：标注在接口方法上
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimit {
    // 每秒允许的请求数（QPS）
    double qps() default 10.0;

    // 限流失败提示信息
    String message() default "请求过于频繁，请稍后重试";
}
