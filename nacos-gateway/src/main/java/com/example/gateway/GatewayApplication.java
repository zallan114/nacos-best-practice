package com.example.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import jakarta.annotation.PreDestroy;

/**
 * 网关启动类
 * 注意：Gateway 是基于 WebFlux 的，不能引入 spring-boot-starter-web 依赖（会冲突）
 */
@SpringBootApplication
@EnableDiscoveryClient // 开启Nacos服务发现
public class GatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);

        // 监听 JVM 关闭信号
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.err.println("=== 应用开始关闭，原因：收到 JVM 关闭信号 ===");
            // 打印线程栈，排查关闭触发源
            Thread.dumpStack();
        }));

    }

    // 打印应用关闭日志
    @PreDestroy
    public void preDestroy() {
        System.err.println("=== 应用执行销毁逻辑，即将退出 ===");
    }
}