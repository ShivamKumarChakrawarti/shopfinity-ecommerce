package com.luxora.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "product_variants")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Product product;

    private String sku;
    private String size;
    private String color;

    private BigDecimal mrpPrice;
    private BigDecimal sellingPrice;

    private Integer stockQuantity;

    private boolean active;
}
