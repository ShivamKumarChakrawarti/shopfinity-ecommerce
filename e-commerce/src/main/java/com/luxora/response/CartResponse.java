package com.luxora.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
public class CartResponse {

    private List<CartItemResponse> items;

    private BigDecimal totalMrp;
    private BigDecimal totalSellingPrice;
    private BigDecimal totalDiscount;
}
