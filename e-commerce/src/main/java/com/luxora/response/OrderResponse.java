package com.luxora.response;

import com.luxora.domain.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class OrderResponse {

    private Long orderId;
    private String orderNumber;
    private OrderStatus status;

    private BigDecimal totalMrp;
    private BigDecimal totalSellingPrice;
    private BigDecimal totalDiscount;

    private LocalDateTime createdAt;
}
