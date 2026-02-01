package com.luxora.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class CartItemResponse {

    private Long productId;
    private String title;

    private BigDecimal mrpPrice;
    private BigDecimal sellingPrice;

    private Integer quantity;
    private Integer discountPercentage;
}
