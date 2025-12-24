package com.example.provider.config;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.example.provider.controller.CustomerController;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * 阿里云通义千问 API 封装客户端（通用 HTTP 调用，无需 Spring AI Starter）
 */
@Component
@RequiredArgsConstructor
public class DashScopeApiClient {

    private final Logger logger = LoggerFactory.getLogger(DashScopeApiClient.class);
    // RestTemplate（Spring 内置 HTTP 客户端，自动注入）
    private final RestTemplate restTemplate;

    // 阿里云 API Key
    @Value("${aliyun.dashscope.api-key}")
    private String apiKey;

    // 通义千问 Chat API 地址
    @Value("${aliyun.dashscope.chat-api-url}")
    private String chatApiUrl;

    // 模型名称
    @Value("${aliyun.dashscope.model}")
    private String model;

    /**
     * 调用通义千问兼容 OpenAI 格式的 API
     */
    public String sendChatRequestOpenAI(String prompt) {
        // 验证提示词不能为空
        if (prompt == null || prompt.trim().isEmpty()) {
            throw new IllegalArgumentException("Prompt cannot be null or empty");
        }
        // 1. 构造请求头（兼容 OpenAI 认证方式）
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);

        // 2. 构造请求体（完全兼容 OpenAI 格式）
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);
        requestBody.put("temperature", 0.2);
        requestBody.put("max_tokens", 500);

        // 消息体（OpenAI 格式）
        List<Map<String, String>> messages = new ArrayList<>();
        Map<String, String> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", prompt);
        messages.add(userMessage);
        requestBody.put("messages", messages);

        // 3. 发送请求
        HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.exchange(chatApiUrl, HttpMethod.POST, httpEntity, String.class);

        // 4. 解析响应（兼容 OpenAI 格式）
        if (response.getStatusCode() == HttpStatus.OK) {
            String responseBody = response.getBody();
            JSONObject jsonObject = JSON.parseObject(responseBody);
            return jsonObject.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content");
        } else {
            throw new RuntimeException("调用通义千问 API 失败，状态码：" + response.getStatusCode());
        }
    }

    /**
     * 调用通义千问 API，获取自然语言回复
     * @param prompt 提示词（包含订单信息和用户问题）
     * @return 模型回复内容
     */
    public String sendChatRequest(String prompt) {
        // 验证提示词不能为空
        if (prompt == null || prompt.trim().isEmpty()) {
            throw new IllegalArgumentException("Prompt cannot be null or empty");
        }
        // 1. 构造请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey); // 通义千问认证方式

        // 2. 构造请求体（按通义千问 API 规范）
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);

        // 消息体（用户角色，传递提示词）
        Map<String, String> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", prompt);

        Map<String, Object> input = new HashMap<>();
        input.put("messages", new Object[] { message });

        // 生成配置
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("temperature", 0.2); // 降低随机性
        parameters.put("max_tokens", 500); // 限制回复长度

        requestBody.put("input", input);
        requestBody.put("parameters", parameters);

        // 3. 发送 POST 请求
        HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.exchange(chatApiUrl, HttpMethod.POST, httpEntity, String.class);

        // 4. 解析响应结果
        if (response.getStatusCode() == HttpStatus.OK) {
            String responseBody = response.getBody();
            JSONObject jsonObject = JSON.parseObject(responseBody);

            logger.info("DashScope API response: {}", jsonObject);

            // 按通义千问 API 返回格式提取内容
            /**
             * {
                "output": {
                    "finish_reason": "stop",
                    "text": "????ORD-1001\n??????\n????????\n???????Mate 60 Pro\n?????2025-12-20T14:30"
                },
                "usage": {
                    "input_tokens": 187,
                    "output_tokens": 50,
                    "prompt_tokens_details": {
                        "cached_tokens": 0
                    },
                    "total_tokens": 237
                },
                "request_id": "b2a0ae39-c894-4da0-ab51-74a38eefcfce"
            }
             */
            // 注意：原生DashScope API返回"text"字段直接在"output"下，而不是"choices"数组
            JSONObject output = jsonObject.getJSONObject("output");
            if (output.containsKey("text")) {
                // 原生DashScope格式
                return output.getString("text");
            } else if (output.containsKey("choices")) {
                // 兼容OpenAI格式
                return output.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content");
            } else {
                throw new RuntimeException("Unexpected API response format: missing 'text' or 'choices' field");
            }
        } else {
            throw new RuntimeException("调用通义千问 API 失败，状态码：" + response.getStatusCode());
        }
    }

    /**
     * 初始化 RestTemplate（Spring Boot 3.2+ 需手动配置，否则可能注入失败）
     */
    @Component
    public static class RestTemplateConfig {

        @org.springframework.context.annotation.Bean
        public RestTemplate restTemplate() {
            return new RestTemplate();
        }
    }
}
