package com.luxora.response;

import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
public class ProductResponse {
    private Long productId;

    private String title;
    private String description;

    private BigDecimal mrpPrice;
    private BigDecimal sellingPrice;
    private Integer discountPercentage;

    private Integer availableQuantity;
    private boolean inStock;

    private String sellerTrustLabel;
}
