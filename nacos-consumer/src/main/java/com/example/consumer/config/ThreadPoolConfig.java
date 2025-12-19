package com.example.consumer.config;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.micrometer.core.instrument.MeterRegistry;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * 线程池配置（CPU密集型+IO密集型）
 */
@Configuration
@ConfigurationProperties(prefix = "thread.pool")
public class ThreadPoolConfig {

    // CPU密集型线程池配置
    private PoolConfig cpu;
    // IO密集型线程池配置
    private PoolConfig io;

    /**
     * 线程池配置内部类
     */
    public static class PoolConfig {

        private int coreSize;
        private int maxSize;
        private int keepAliveSeconds;
        private int queueSize;
        private String poolName;

        // Getters and setters
        public int getCoreSize() {
            return coreSize;
        }

        public void setCoreSize(int coreSize) {
            this.coreSize = coreSize;
        }

        public int getMaxSize() {
            return maxSize;
        }

        public void setMaxSize(int maxSize) {
            this.maxSize = maxSize;
        }

        public int getKeepAliveSeconds() {
            return keepAliveSeconds;
        }

        public void setKeepAliveSeconds(int keepAliveSeconds) {
            this.keepAliveSeconds = keepAliveSeconds;
        }

        public int getQueueSize() {
            return queueSize;
        }

        public void setQueueSize(int queueSize) {
            this.queueSize = queueSize;
        }

        public String getPoolName() {
            return poolName;
        }

        public void setPoolName(String poolName) {
            this.poolName = poolName;
        }

        // equals, hashCode and toString

        @Override
        public String toString() {
            return (
                "PoolConfig{" +
                "coreSize=" +
                coreSize +
                ", maxSize=" +
                maxSize +
                ", keepAliveSeconds=" +
                keepAliveSeconds +
                ", queueSize=" +
                queueSize +
                ", poolName='" +
                poolName +
                '\'' +
                '}'
            );
        }
    }

    // Getters and setters
    public PoolConfig getCpu() {
        return cpu;
    }

    public void setCpu(PoolConfig cpu) {
        this.cpu = cpu;
    }

    public PoolConfig getIo() {
        return io;
    }

    public void setIo(PoolConfig io) {
        this.io = io;
    }

    // equals, hashCode and toString

    @Override
    public String toString() {
        return "ThreadPoolConfig{" + "cpu=" + cpu + ", io=" + io + '}';
    }

    /**
     * CPU密集型线程池Bean
     */
    @Bean(name = ThreadPoolConstant.CPU_THREAD_POOL)
    public MonitoredThreadPoolExecutor cpuThreadPool(MeterRegistry meterRegistry, StringRedisTemplate redisTemplate) {
        return buildThreadPool(cpu, meterRegistry, redisTemplate);
    }

    /**
     * IO密集型线程池Bean
     */
    @Bean(name = ThreadPoolConstant.IO_THREAD_POOL)
    public MonitoredThreadPoolExecutor ioThreadPool(MeterRegistry meterRegistry, StringRedisTemplate redisTemplate) {
        return buildThreadPool(io, meterRegistry, redisTemplate);
    }

    /**
     * 构建线程池通用方法
     */
    private MonitoredThreadPoolExecutor buildThreadPool(PoolConfig config, MeterRegistry meterRegistry, StringRedisTemplate redisTemplate) {
        // 1. 自定义线程工厂（命名+守护线程）
        ThreadFactory threadFactory = new ThreadFactoryBuilder()
            .setNameFormat(config.getPoolName() + "-%d")
            .setDaemon(true) // 守护线程，避免阻塞JVM退出
            .setPriority(Thread.NORM_PRIORITY)
            .build();

        // 2. 可扩容阻塞队列
        ResizableBlockingQueue<Runnable> queue = new ResizableBlockingQueue<>(config.getQueueSize());

        // 3. 自定义拒绝策略（存入Redis）
        CustomRejectedExecutionHandler rejectedHandler = new CustomRejectedExecutionHandler(redisTemplate, config.getPoolName());

        // 4. 构建监控线程池
        return new MonitoredThreadPoolExecutor(
            config.getCoreSize(),
            config.getMaxSize(),
            config.getKeepAliveSeconds(),
            TimeUnit.SECONDS,
            queue,
            threadFactory,
            rejectedHandler,
            config.getPoolName(),
            meterRegistry,
            redisTemplate
        );
    }
}
