package com.luxora.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class VariantCreateRequest {
    private String sku;
    private String size;        // S, M, L
    private String color;
    private BigDecimal mrpPrice;
    private BigDecimal sellingPrice;
    private Integer stockQuantity;
}
