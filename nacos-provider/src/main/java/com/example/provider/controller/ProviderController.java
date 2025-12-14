package com.example.provider.controller;

import com.example.common.entity.User;
import com.example.common.utils.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/provider")
public class ProviderController {

    // 获取当前端口（用于验证负载均衡）
    @Value("${server.port}")
    private String port;

    /**
     * 根据ID查询用户
     */
    @GetMapping("/user/{id}")
    public Result<User> getUser(@PathVariable Long id) {
        User user = new User();
        user.setId(id);
        user.setName("Provider-" + port);
        user.setAge(20 + id.intValue());
        return Result.success(user);
    }

    /**
     * 测试配置动态刷新（后续结合配置中心）
     */
    @Value("${custom.message:默认消息}")
    private String customMessage;

    @GetMapping("/config")
    public Result<String> getConfig() {
        return Result.success("当前端口：" + port + "，配置消息：" + customMessage);
    }
}
