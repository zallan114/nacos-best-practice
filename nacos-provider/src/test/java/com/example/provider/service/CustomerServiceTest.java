package com.example.provider.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.example.provider.config.DashScopeApiClient;
import com.example.provider.entity.Order;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.LoggerFactory;

class CustomerServiceTest {

    @Mock
    private DashScopeApiClient dashScopeApiClient;

    @Mock
    private OrderService orderService;

    @InjectMocks
    private CustomerService customerService;

    // Prompt template from application.yml
    private static final String PROMPT_TEMPLATE =
        """
            你的任务是作为电商客服助手，完成以下步骤：
            1. 从用户的问题中提取关键信息：订单号（格式：ORD-xxxx，xxxx为数字）、用户名（可选）
            2. 如果无法提取订单号，回复："抱歉，我未找到你的订单号，请提供格式为ORD-xxxx的订单号（xxxx为数字）"
            3. 如果提取到订单号，调用订单查询接口，将查询结果整理为自然、友好的回复，无需额外无关内容
            订单查询结果格式参考：
            订单号：{orderNo}
            用户名：{username}
            订单状态：{status}（待付款/已付款/已发货/已完成/已取消）
            商品名称：{productName}
            下单时间：{createTime}
        """;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Set the prompt template using reflection since it's a private field
        try {
            var field = CustomerService.class.getDeclaredField("promptTemplate");
            field.setAccessible(true);
            field.set(customerService, PROMPT_TEMPLATE);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set promptTemplate", e);
        }
    }

    @Test
    void testHandleCustomerQuery_WithValidOrderNumber() throws Exception {
        // Arrange
        String userQuestion = "我的订单ORD-1001发货了吗？";
        String expectedReply = "您好，您的订单ORD-1001已发货，商品为华为Mate 60 Pro。";

        // Mock order service
        Order order = new Order();
        order.setOrderNo("ORD-1001");
        order.setUsername("张三");
        order.setStatus("已发货");
        order.setProductName("华为Mate 60 Pro");
        order.setCreateTime(LocalDateTime.now());

        when(orderService.getOrderByOrderNo("ORD-1001")).thenReturn(Optional.of(order));
        when(dashScopeApiClient.sendChatRequest(anyString())).thenReturn(expectedReply);

        // Act
        String result = customerService.handleCustomerQuery(userQuestion);

        // Assert
        assertEquals(expectedReply, result);

        // Verify interactions
        verify(orderService, times(1)).getOrderByOrderNo("ORD-1001");
        verify(dashScopeApiClient, times(1)).sendChatRequest(anyString());
    }

    @Test
    void testHandleCustomerQuery_WithoutOrderNumber() {
        // Arrange
        String userQuestion = "我的订单发货了吗？";
        String expectedReply = "抱歉，我未找到你的订单号，请提供格式为ORD-xxxx的订单号（xxxx为数字）";

        // Act
        String result = customerService.handleCustomerQuery(userQuestion);

        // Assert
        assertEquals(expectedReply, result);

        // Verify no calls to dependencies
        verifyNoInteractions(orderService);
        verifyNoInteractions(dashScopeApiClient);
    }

    @Test
    void testHandleCustomerQuery_WithInvalidOrderNumber() {
        // Arrange
        String userQuestion = "帮我查一下ORD-9999的订单状态";
        String expectedReply = "抱歉，未查询到订单号为【ORD-9999】的订单信息，请确认订单号是否正确。";

        // Mock order service - order not found
        when(orderService.getOrderByOrderNo("ORD-9999")).thenReturn(Optional.empty());

        // Act
        String result = customerService.handleCustomerQuery(userQuestion);

        // Assert
        assertEquals(expectedReply, result);

        // Verify interactions
        verify(orderService, times(1)).getOrderByOrderNo("ORD-9999");
        verifyNoInteractions(dashScopeApiClient);
    }

    @Test
    void testHandleCustomerQuery_WithDashScopeApiError() throws Exception {
        // Arrange
        String userQuestion = "ORD-1002的订单现在是什么状态？";
        String expectedReply = "抱歉，当前客服系统暂时无法响应，请稍后再试~";

        // Mock order service
        Order order = new Order();
        order.setOrderNo("ORD-1002");
        order.setUsername("李四");
        order.setStatus("待付款");
        order.setProductName("苹果iPhone 15");
        order.setCreateTime(LocalDateTime.now());

        when(orderService.getOrderByOrderNo("ORD-1002")).thenReturn(Optional.of(order));
        when(dashScopeApiClient.sendChatRequest(anyString())).thenThrow(new RuntimeException("API Connection Error"));

        // Act
        String result = customerService.handleCustomerQuery(userQuestion);

        // Assert
        assertEquals(expectedReply, result);

        // Verify interactions
        verify(orderService, times(1)).getOrderByOrderNo("ORD-1002");
        verify(dashScopeApiClient, times(1)).sendChatRequest(anyString());
    }

    @Test
    void testHandleCustomerQuery_WithDifferentOrderNumberFormats() {
        // Test various valid order number formats
        String[] validOrderQuestions = {
            "我的订单是ORD-1234，请问发货了吗？",
            "帮我查下ORD-5678的订单状态",
            "ORD-9012 到哪了？",
            "我想知道ORD-3456的情况",
        };

        // Mock order service
        Order order = new Order();
        order.setOrderNo("ORD-1234");
        order.setUsername("测试用户");
        order.setStatus("已完成");
        order.setProductName("测试商品");
        order.setCreateTime(LocalDateTime.now());

        // For simplicity, mock all order numbers to return the same order
        when(orderService.getOrderByOrderNo(anyString())).thenReturn(Optional.of(order));
        when(dashScopeApiClient.sendChatRequest(anyString())).thenReturn("测试回复");

        for (String question : validOrderQuestions) {
            // Act
            String result = customerService.handleCustomerQuery(question);

            // Assert
            assertEquals("测试回复", result);
        }

        // Verify interactions - 4 calls for 4 test questions
        verify(orderService, times(4)).getOrderByOrderNo(anyString());
        verify(dashScopeApiClient, times(4)).sendChatRequest(anyString());
    }
}
