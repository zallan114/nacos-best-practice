package com.example.consumer.controller;

import com.example.common.entity.User;
import com.example.common.utils.Result;
import com.example.consumer.feign.ProviderFeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/consumer")
public class ConsumerController {

    @Autowired
    private ProviderFeignClient providerFeignClient;

    /**
     * 调用提供者接口
     */
    @GetMapping("/user/{id}")
    public Result<User> getUser(@PathVariable Long id) {
        return providerFeignClient.getUser(id);
    }

    /**
     * 调用提供者配置接口
     */
    @GetMapping("/config")
    public Result<String> getConfig() {
        return providerFeignClient.getConfig();
    }
}
