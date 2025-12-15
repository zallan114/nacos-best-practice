package com.example.gateway.config;

 

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.regex.Pattern;

/**
 * 全局过滤器：拦截非法 HTTP 请求
 */
@Component
public class IllegalRequestFilter implements GlobalFilter, Ordered {

    Logger logger = LoggerFactory.getLogger(IllegalRequestFilter.class);

    // 匹配标准 HTTP 请求行的正则（简化版）
    private static final Pattern VALID_HTTP_METHOD = Pattern.compile("^(GET|POST|PUT|DELETE|HEAD|OPTIONS|PATCH)$");
    private static final Pattern VALID_HTTP_VERSION = Pattern.compile("^HTTP/1\\.[01]$");

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        try {
            // 1. 校验请求方法
            String method = request.getMethod().name();
            logger.info("请求方法[slf4j]: {}", method);
            if (!VALID_HTTP_METHOD.matcher(method).matches()) {
                return rejectRequest(exchange, "非法请求方法");
            }

            // 2. 校验 HTTP 版本（Reactor HTTP 底层可通过请求行解析）
            //String rawRequestLine = request.getPath().contextPath().value(); // 或从请求头解析原始请求行
            //if (rawRequestLine != null && !VALID_HTTP_VERSION.matcher(rawRequestLine).find()) {
            //    return rejectRequest(exchange, "非法 HTTP 版本");
            //}

            // 3. 校验请求头是否为空（排除空请求）
            // HttpHeaders headers = request.getHeaders();
            // if (headers.isEmpty()) {
            //     // 使用响应式方式检查请求体是否为空
            //     logger.info("请求头为空，检查请求体是否为空");
            //     return request.getBody()
            //         .next()
            //         .hasElement()
            //         .map(has -> !has)
            //         .flatMap(isEmpty -> isEmpty ? rejectRequest(exchange, "空请求") : chain.filter(exchange));
                   
            // }

            // 合法请求，继续转发
            return chain.filter(exchange);
        } catch (Exception e) {
            // 捕获解码异常，直接拒绝
            return rejectRequest(exchange, "请求解码失败");
        }
    }

    // 拒绝非法请求，返回 400
    private Mono<Void> rejectRequest(ServerWebExchange exchange, String msg) {
        exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
        exchange.getResponse().getHeaders().add("Content-Type", "application/json");
        String response = "{\"code\":400,\"msg\":\"" + msg + "\"}";
        return exchange.getResponse().writeWith(
            Mono.just(exchange.getResponse().bufferFactory().wrap(response.getBytes()))
        );
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE; // 最高优先级，先于路由执行
    }
}
