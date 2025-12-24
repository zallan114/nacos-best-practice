package com.example.provider.service;

import com.example.provider.entity.Order;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Service;

/**
 * 订单服务（模拟数据库存储，实际项目可替换为MyBatis/MyBatis-Plus）
 */
@Service
public class OrderService {

    /**
     * 模拟订单数据库
     */
    private static final Map<String, Order> ORDER_DATABASE = new HashMap<>();

    // 初始化测试数据
    static {
        ORDER_DATABASE.put(
            "ORD-1001",
            new Order("ORD-1001", "张三", "已发货", "华为Mate 60 Pro", LocalDateTime.of(2025, 12, 20, 14, 30, 0))
        );
        ORDER_DATABASE.put("ORD-1002", new Order("ORD-1002", "李四", "待付款", "苹果iPhone 15", LocalDateTime.of(2025, 12, 21, 10, 15, 0)));
        ORDER_DATABASE.put(
            "ORD-1003",
            new Order("ORD-1003", "王五", "已完成", "小米SU7 汽车模型", LocalDateTime.of(2025, 12, 18, 9, 0, 0))
        );
    }

    /**
     * 根据订单号查询订单
     * @param orderNo 订单号
     * @return 订单信息（空则返回Optional.empty()）
     */
    public Optional<Order> getOrderByOrderNo(String orderNo) {
        if (orderNo == null || orderNo.trim().isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(ORDER_DATABASE.get(orderNo.trim()));
    }
}
