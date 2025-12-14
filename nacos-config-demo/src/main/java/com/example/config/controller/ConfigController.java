package com.example.config.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @RefreshScope：开启配置动态刷新
 */
@RestController
@RequestMapping("/config-demo")
@RefreshScope // 开启配置动态刷新（需在bootstrap.yml中配置 extension-configs.refresh: true）
public class ConfigController {

    // 从Nacos配置中心读取 - 配置管理\配置列表\nacos-config-demo.yml
    @Value("${custom.message:默认消息}")
    private String customMessage;

    @Value("${custom.env:dev}")
    private String customEnv;

    // 从扩展配置common.yml读取( extension-configs in bootstrap.yml) - 配置管理\配置列表\common.yml
    @Value("${common.version:1.0.0}")
    private String commonVersion;

    @GetMapping("/info")
    public String getConfigInfo() {
        return String.format(
            "custom.message: %s<br/>custom.env: %s<br/>common.version: %s",
            customMessage, customEnv, commonVersion
        );
    }
}

