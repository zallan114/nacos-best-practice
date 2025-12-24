package com.example.provider.controller;

import com.example.provider.service.CustomerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

/**
 * Unit tests for CustomerController
 */
@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerService customerService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        // Reset mock before each test
        Mockito.reset(customerService);
    }

    @Test
    void testQueryOrderStatus_WithValidOrderNumber() throws Exception {
        // Arrange
        String userQuestion = "我的订单ORD-1001发货了吗？";
        String expectedReply = "订单ORD-1001已发货";

        Mockito.when(customerService.handleCustomerQuery(userQuestion)).thenReturn(expectedReply);

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("userQuestion", userQuestion);

        // Act & Assert
        mockMvc
            .perform(
                MockMvcRequestBuilders.post("/api/query-order")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestBody))
            )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.reply").value(expectedReply));

        // Verify service method was called
        Mockito.verify(customerService, Mockito.times(1)).handleCustomerQuery(userQuestion);
    }

    @Test
    void testQueryOrderStatus_WithAnotherValidOrderNumber() throws Exception {
        // Arrange
        String userQuestion = "帮我查一下ORD-9999的订单状态";
        String expectedReply = "订单ORD-9999状态：已完成";

        Mockito.when(customerService.handleCustomerQuery(userQuestion)).thenReturn(expectedReply);

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("userQuestion", userQuestion);

        // Act & Assert
        mockMvc
            .perform(
                MockMvcRequestBuilders.post("/api/query-order")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestBody))
            )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.reply").value(expectedReply));
    }

    @Test
    void testQueryOrderStatus_WithoutOrderNumber() throws Exception {
        // Arrange
        String userQuestion = "我的订单发货了吗？";
        String expectedReply = "抱歉，我未找到你的订单号，请提供格式为ORD-xxxx的订单号（xxxx为数字）";

        Mockito.when(customerService.handleCustomerQuery(userQuestion)).thenReturn(expectedReply);

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("userQuestion", userQuestion);

        // Act & Assert
        mockMvc
            .perform(
                MockMvcRequestBuilders.post("/api/query-order")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestBody))
            )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.reply").value(expectedReply));
    }

    @Test
    void testQueryOrderStatus_WithDirectOrderNumber() throws Exception {
        // Arrange
        String userQuestion = "ORD-1002的订单现在是什么状态？";
        String expectedReply = "订单ORD-1002状态：待付款";

        Mockito.when(customerService.handleCustomerQuery(userQuestion)).thenReturn(expectedReply);

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("userQuestion", userQuestion);

        // Act & Assert
        mockMvc
            .perform(
                MockMvcRequestBuilders.post("/api/query-order")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestBody))
            )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.reply").value(expectedReply));
    }

    @Test
    void testQueryOrderStatus_WithEmptyUserQuestion() throws Exception {
        // Arrange
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("userQuestion", "");

        // Act & Assert
        mockMvc
            .perform(
                MockMvcRequestBuilders.post("/api/query-order")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestBody))
            )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.reply").value("抱歉，你的问题不能为空，请输入查询内容。"));

        // Verify service method was NOT called
        Mockito.verify(customerService, Mockito.never()).handleCustomerQuery(Mockito.anyString());
    }

    @Test
    void testQueryOrderStatus_WithNullUserQuestion() throws Exception {
        // Arrange
        Map<String, String> requestBody = new HashMap<>();
        // userQuestion is null

        // Act & Assert
        mockMvc
            .perform(
                MockMvcRequestBuilders.post("/api/query-order")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestBody))
            )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.reply").value("抱歉，你的问题不能为空，请输入查询内容。"));

        // Verify service method was NOT called
        Mockito.verify(customerService, Mockito.never()).handleCustomerQuery(Mockito.anyString());
    }
}
