package com.example.consumer.feign;

import com.example.common.entity.User;
import com.example.common.utils.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// 指向服务提供者的服务名
@FeignClient(name = "nacos-provider")
public interface ProviderFeignClient {

    @GetMapping("/provider/user/{id}")
    Result<User> getUser(@PathVariable("id") Long id);

    @GetMapping("/provider/config")
    Result<String> getConfig();
}
