package com.example.provider.service;

// import org.springframework.ai.chat.ChatClient;
// import org.springframework.ai.chat.ChatResponse;
// import org.springframework.ai.chat.prompt.Prompt;
// import org.springframework.ai.chat.prompt.PromptTemplate;
import com.example.provider.config.DashScopeApiClient;
import com.example.provider.controller.CustomerController;
import com.example.provider.entity.Order;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 智能客服核心服务：解析用户问题 + 查询订单 + 生成自然语言回复
 */
@Service
@RequiredArgsConstructor
public class CustomerService {

    private final Logger logger = LoggerFactory.getLogger(CustomerService.class);

    // Spring AI 聊天客户端（自动注入）
    // private final ChatClient chatClient;、

    // 通义千问 API 客户端
    private final DashScopeApiClient dashScopeApiClient;

    // 订单服务
    private final OrderService orderService;

    // 从配置文件读取提示词模板
    @Value("${ecommerce.customer.service.prompt-template}")
    private String promptTemplate;

    // 正则表达式：提取订单号（匹配 ORD-xxxx 格式）
    private static final Pattern ORDER_NO_PATTERN = Pattern.compile("ORD-\\d{4}");

    /**
     * 处理用户客服查询请求
     * @param userQuestion 用户自然语言问题（如："我的订单ORD-1001发货了吗？"）
     * @return 智能回复
     */
    public String handleCustomerQuery(String userQuestion) {
        // 步骤1：从用户问题中提取订单号
        String orderNo = extractOrderNo(userQuestion);
        logger.info("Extracted orderNo: {}", orderNo);

        if (orderNo == null) {
            // 未提取到订单号，直接返回提示
            return "抱歉，我未找到你的订单号，请提供格式为ORD-xxxx的订单号（xxxx为数字）";
        }

        // 步骤2：根据订单号查询订单
        Optional<Order> orderOptional = orderService.getOrderByOrderNo(orderNo);
        logger.info("Order found: {}", orderOptional);
        if (orderOptional.isEmpty()) {
            return String.format("抱歉，未查询到订单号为【%s】的订单信息，请确认订单号是否正确。", orderNo);
        }
        Order order = orderOptional.get();

        // 步骤3：构造大模型请求参数，生成自然语言回复
        // Map<String, Object> promptParams = new HashMap<>();
        // promptParams.put("orderNo", order.getOrderNo());
        // promptParams.put("username", order.getUsername());
        // promptParams.put("status", order.getStatus());
        // promptParams.put("productName", order.getProductName());
        // promptParams.put("createTime", order.getCreateTime().toString());
        // promptParams.put("userQuestion", userQuestion);

        // // 渲染提示词模板
        // PromptTemplate template = new PromptTemplate(promptTemplate);
        // Prompt prompt = template.create(promptParams);

        // // 调用大模型并获取回复
        // ChatResponse response = chatClient.call(prompt);
        // return response.getResult().getOutput().getContent();

        // 步骤3：渲染提示词模板
        String prompt = renderPromptTemplate(order, userQuestion);
        logger.info("Rendered prompt: {}", prompt);

        // 步骤4：调用通义千问 API 获取回复
        try {
            return dashScopeApiClient.sendChatRequest(prompt);
        } catch (Exception e) {
            logger.error("Error while calling DashScope API: {}", e.getMessage(), e);
            return "抱歉，当前客服系统暂时无法响应，请稍后再试~";
        }
    }

    /**
     * 从用户问题中提取订单号
     * @param userQuestion 用户问题
     * @return 订单号（null表示未提取到）
     */
    private String extractOrderNo(String userQuestion) {
        if (userQuestion == null || userQuestion.trim().isEmpty()) {
            return null;
        }
        Matcher matcher = ORDER_NO_PATTERN.matcher(userQuestion);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }

    /**
     * 渲染提示词模板
     */
    private String renderPromptTemplate(Order order, String userQuestion) {
        Map<String, String> params = new HashMap<>();
        params.put("orderNo", order.getOrderNo());
        params.put("username", order.getUsername());
        params.put("status", order.getStatus());
        params.put("productName", order.getProductName());
        params.put("createTime", order.getCreateTime().toString());
        params.put("userQuestion", userQuestion);

        // 简单模板替换（也可使用 Freemarker 实现复杂模板渲染）
        String prompt = promptTemplate;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            prompt = prompt.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return prompt;
    }
}
