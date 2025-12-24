package com.example.provider.entity;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 订单实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    /**
     * 订单号（主键）
     */
    private String orderNo;

    /**
     * 用户名
     */
    private String username;

    /**
     * 订单状态：待付款/已付款/已发货/已完成/已取消
     */
    private String status;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 下单时间
     */
    private LocalDateTime createTime;
}
