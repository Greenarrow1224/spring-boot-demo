package com.example.mq.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author greenarrow
 * @version 1.0.0
 * @description 订单支付事件数据传输对象
 * @date 2022-10-31 13:37
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderPaidEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    private String orderId;

    private BigDecimal paidMoney;

    private String msg;
}
