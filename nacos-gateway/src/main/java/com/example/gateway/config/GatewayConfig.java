package com.example.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 自定义路由规则（和yml配置二选一即可，推荐yml配置更直观）
 */
@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // 示例：额外添加一个路由，/test/** 转发到百度
                .route("test_route", r -> r.path("/test/**")
                        .uri("https://www.baidu.com"))
                .build();
    }
}