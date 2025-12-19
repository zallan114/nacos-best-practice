package com.example.consumer.config;

public class ThreadPoolConstant {

    // 线程池Bean名称
    public static final String CPU_THREAD_POOL = "cpuThreadPool";
    public static final String IO_THREAD_POOL = "ioThreadPool";

    // Redis拒绝任务存储前缀
    public static final String REJECTED_TASK_KEY_PREFIX = "rejected_task:";
}
