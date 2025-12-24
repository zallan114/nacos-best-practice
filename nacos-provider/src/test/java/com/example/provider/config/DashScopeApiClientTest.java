package com.example.provider.config;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

/**
 * Integration tests for DashScopeApiClient with real API calls
 */
@Disabled("跳过真实API调用测试")
@SpringBootTest
@TestPropertySource(
    properties = {
        // 替换为你的真实API密钥
        "aliyun.dashscope.api-key=sk-7054",
        // 真实的API地址（使用原生DashScope格式的端点）
        "aliyun.dashscope.chat-api-url=https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation",
        // 真实的模型名称
        "aliyun.dashscope.model=qwen-plus",
    }
)
class DashScopeApiClientTest {

    @Autowired
    private DashScopeApiClient dashScopeApiClient;

    /**
     * 测试真实的sendChatRequest调用
     */
    @Test
    void testSendChatRequest_RealApiCall() {
        // 简单的测试提示词
        String prompt = "请简单介绍一下你自己，用中文回答，不超过50字。";

        // 执行真实API调用
        String result = dashScopeApiClient.sendChatRequest(prompt);

        // 验证结果
        assertNotNull(result);
        assertFalse(result.isEmpty());
        System.out.println("真实API调用结果(sendChatRequest): " + result);
    }

    /**
     * 测试真实的sendChatRequestOpenAI调用
     */
    @Test
    void testSendChatRequestOpenAI_RealApiCall() {
        // 简单的测试提示词
        String prompt = "请简单介绍一下你自己，用中文回答，不超过50字。";

        // 执行真实API调用
        String result = dashScopeApiClient.sendChatRequestOpenAI(prompt);

        // 验证结果
        assertNotNull(result);
        assertFalse(result.isEmpty());
        System.out.println("真实API调用结果(sendChatRequestOpenAI): " + result);
    }

    /**
     * 测试空提示词处理（不调用API）
     */
    @Test
    void testSendChatRequest_EmptyPrompt() {
        // 验证空提示词会抛出异常
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            dashScopeApiClient.sendChatRequest("");
        });

        assertEquals("Prompt cannot be null or empty", exception.getMessage());
    }

    /**
     * 测试null提示词处理（不调用API）
     */
    @Test
    void testSendChatRequest_NullPrompt() {
        // 验证null提示词会抛出异常
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            dashScopeApiClient.sendChatRequest(null);
        });

        assertEquals("Prompt cannot be null or empty", exception.getMessage());
    }
}
// 注意：
// 1. 真实API调用会产生费用，请谨慎运行
// 2. 请确保你的API密钥有效且有足够的余额
// 3. 如果不需要运行真实API测试，可以将测试类注释或重命名
// 4. 建议仅在开发和调试阶段使用真实API测试，生产环境使用mock测试
