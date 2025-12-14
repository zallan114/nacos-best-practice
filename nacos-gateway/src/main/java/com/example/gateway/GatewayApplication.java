package com.example.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 网关启动类
 * 注意：Gateway 是基于 WebFlux 的，不能引入 spring-boot-starter-web 依赖（会冲突）
 */
@SpringBootApplication
@EnableDiscoveryClient // 开启Nacos服务发现
public class GatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }
}