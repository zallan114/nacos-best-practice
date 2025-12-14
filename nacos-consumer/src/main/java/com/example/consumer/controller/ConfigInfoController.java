package com.example.consumer.controller;

import com.example.consumer.feign.ConfigFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/consumer/config-demo")
public class ConfigInfoController {

    @Autowired
    private ConfigFeignClient configFeignClient;

    /**
     * 调用nacos-config-demo服务的配置信息接口
     */
    @GetMapping("/info")
    public String getConfigInfo() {
        return configFeignClient.getConfigDemoInfo();
    }
}