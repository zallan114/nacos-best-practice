package com.example.provider.controller;

import com.example.provider.service.CustomerService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 客服接口控制层：提供HTTP接口接收用户查询
 */
@RestController
@RequestMapping("/provider")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;
    private final Logger logger = LoggerFactory.getLogger(CustomerController.class);

    /**
     * 订单状态查询接口
     * @param requestBody 请求体（包含 userQuestion 字段）
     * @return 智能回复结果
     */
    @PostMapping("/api/query-order")
    public Map<String, String> queryOrderStatus(@RequestBody Map<String, String> requestBody) {
        // 获取用户问题
        String userQuestion = requestBody.get("userQuestion");
        logger.info("Received query: {}", userQuestion);

        if (userQuestion == null || userQuestion.trim().isEmpty()) {
            return Map.of("reply", "抱歉，你的问题不能为空，请输入查询内容。");
        }

        // 调用客服服务处理查询
        String reply = customerService.handleCustomerQuery(userQuestion);
        return Map.of("reply", reply);
    }
}
/**
{
  "userQuestion": "我的订单ORD-1001发货了吗？"
}


{
  "userQuestion": "帮我查一下ORD-9999的订单状态"
}

{
  "userQuestion": "我的订单发货了吗？"
}

{
  "userQuestion": "ORD-1002的订单现在是什么状态？"
}
 */
