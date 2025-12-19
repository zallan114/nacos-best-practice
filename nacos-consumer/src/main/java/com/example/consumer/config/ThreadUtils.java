package com.example.consumer.config;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadUtils {

    /**
     任务拒绝策略（RejectedExecutionHandler）
        避免使用默认的AbortPolicy（直接抛异常），根据业务选择：
        CallerRunsPolicy：调用者线程执行（适合非核心任务，如日志打印）
        DiscardOldestPolicy：丢弃队列最旧任务（适合实时性要求高的任务，如秒杀下单）
        DiscardPolicy：静默丢弃新任务（适合可丢失任务，如统计上报）
        自定义策略：实现RejectedExecutionHandler接口（如任务存入 MQ 重试）
     */

    // 获取CPU核心数（含超线程）
    int cpuCore = Runtime.getRuntime().availableProcessors();
    ThreadPoolExecutor cpuExecutor = new ThreadPoolExecutor(
        cpuCore + 1, // 核心线程数
        cpuCore * 2, // 最大线程数
        60L, // 空闲线程存活时间（非核心线程）
        TimeUnit.SECONDS,
        new LinkedBlockingQueue<>(), // 无界队列
        new ThreadFactoryBuilder().setNameFormat("cpu-task-%d").build(), // 命名线程池
        new ThreadPoolExecutor.DiscardOldestPolicy() // 任务拒绝策略（按业务调整）
    );

    ThreadPoolExecutor ioExecutor = new ThreadPoolExecutor(
        cpuCore * 2 + 1, // 核心线程数
        cpuCore * 10, // 最大线程数
        30L, // 空闲线程存活时间（IO任务线程可快速回收）
        TimeUnit.SECONDS,
        new ArrayBlockingQueue<>(500), // 有界队列（容量500）
        new ThreadFactoryBuilder().setNameFormat("io-task-%d").build(),
        new ThreadPoolExecutor.CallerRunsPolicy() // 拒绝策略：调用者线程执行（避免任务丢失）
    );
}
