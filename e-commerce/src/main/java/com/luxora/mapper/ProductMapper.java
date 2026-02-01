package com.luxora.mapper;

import com.luxora.entity.Product;
import com.luxora.response.ProductResponse;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ProductMapper {

    public ProductResponse mapToResponse(Product product) {

        return new ProductResponse(
                product.getId(),
                product.getTitle(),
                product.getDescription(),
                product.getMrpPrice(),
                product.getSellingPrice(),
                calculateDiscountPercentage(product),
                product.getAvailableQuantity(),
                product.getAvailableQuantity() > 0,
                null
        );
    }

    // ---------- PRICING LOGIC (SINGLE SOURCE) ----------
    public int calculateDiscountPercentage(Product product) {

        if (product.getMrpPrice() == null
                || product.getSellingPrice() == null
                || product.getMrpPrice().compareTo(BigDecimal.ZERO) <= 0) {
            return 0;
        }

        return product.getMrpPrice()
                .subtract(product.getSellingPrice())
                .multiply(BigDecimal.valueOf(100))
                .divide(product.getMrpPrice(), RoundingMode.HALF_UP)
                .intValue();
    }

}

