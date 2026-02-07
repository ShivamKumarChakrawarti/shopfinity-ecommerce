package com.luxora.response;

import com.luxora.domain.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class PaymentOrderResponse {

    private Long paymentOrderId;
    private String gatewayOrderId;
    private BigDecimal amount;
    private PaymentMethod paymentMethod;
}
