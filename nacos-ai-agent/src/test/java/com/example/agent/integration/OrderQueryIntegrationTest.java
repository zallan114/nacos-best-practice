package com.example.agent.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.example.agent.AiAgentApplication;
import com.example.agent.dto.OrderQueryRequest;
import com.example.agent.dto.OrderQueryResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

/**
 * 订单查询集成测试
 * 测试完整的调用流程：Controller -> AI Agent -> Tool -> Service
 */
@SpringBootTest(
    classes = AiAgentApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = "spring.profiles.active=test"
)
public class OrderQueryIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String getBaseUrl() {
        return "http://localhost:" + port;
    }

    @Test
    public void testQueryRecent7DaysOrders() {
        // 构造请求数据
        OrderQueryRequest request = new OrderQueryRequest();
        request.setUserId("user001");
        request.setQueryText("查询我最近7天的订单");

        // 设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<OrderQueryRequest> entity = new HttpEntity<>(request, headers);

        // 发送请求
        OrderQueryResponse response = restTemplate
            .exchange(getBaseUrl() + "/agent/order/query", HttpMethod.POST, entity, OrderQueryResponse.class)
            .getBody();

        // 验证响应
        assertNotNull(response, "响应不能为空");
        assertEquals(200, response.getCode(), "响应码应为200");
        assertNotNull(response.getReplyContent(), "回复内容不能为空");
        System.out.println("查询结果：" + response.getReplyContent());
    }

    @Test
    public void testQueryAllOrders() {
        // 构造请求数据
        OrderQueryRequest request = new OrderQueryRequest();
        request.setUserId("user001");
        request.setQueryText("查询我的所有订单");

        // 设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<OrderQueryRequest> entity = new HttpEntity<>(request, headers);

        // 发送请求
        OrderQueryResponse response = restTemplate
            .exchange(getBaseUrl() + "/agent/order/query", HttpMethod.POST, entity, OrderQueryResponse.class)
            .getBody();

        // 验证响应
        assertNotNull(response, "响应不能为空");
        assertEquals(200, response.getCode(), "响应码应为200");
        assertNotNull(response.getReplyContent(), "回复内容不能为空");
        System.out.println("查询结果：" + response.getReplyContent());
    }

    @Test
    public void testQueryOrderByNo() {
        // 构造请求数据
        OrderQueryRequest request = new OrderQueryRequest();
        request.setUserId("user001");
        request.setQueryText("查询订单号为20251225001的订单");

        // 设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<OrderQueryRequest> entity = new HttpEntity<>(request, headers);

        // 发送请求
        OrderQueryResponse response = restTemplate
            .exchange(getBaseUrl() + "/agent/order/query", HttpMethod.POST, entity, OrderQueryResponse.class)
            .getBody();

        // 验证响应
        assertNotNull(response, "响应不能为空");
        assertEquals(200, response.getCode(), "响应码应为200");
        assertNotNull(response.getReplyContent(), "回复内容不能为空");
        System.out.println("查询结果：" + response.getReplyContent());
    }

    @Test
    public void testQueryOrdersByStatus() {
        // 构造请求数据
        OrderQueryRequest request = new OrderQueryRequest();
        request.setUserId("user001");
        request.setQueryText("查询我的已完成订单");

        // 设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<OrderQueryRequest> entity = new HttpEntity<>(request, headers);

        // 发送请求
        OrderQueryResponse response = restTemplate
            .exchange(getBaseUrl() + "/agent/order/query", HttpMethod.POST, entity, OrderQueryResponse.class)
            .getBody();

        // 验证响应
        assertNotNull(response, "响应不能为空");
        assertEquals(200, response.getCode(), "响应码应为200");
        assertNotNull(response.getReplyContent(), "回复内容不能为空");
        System.out.println("查询结果：" + response.getReplyContent());
    }
}
