package com.example.consumer.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

// 指向nacos-config-demo服务
@FeignClient(name = "nacos-config-demo")
public interface ConfigFeignClient {

    @GetMapping("/config-demo/info")
    String getConfigDemoInfo();
}