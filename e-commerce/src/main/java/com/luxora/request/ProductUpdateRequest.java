package com.luxora.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductUpdateRequest {
    @NotBlank(message = "Product title is required")
    private String title;

    @Size(max = 2000, message = "Description cannot exceed 2000 characters")
    private String description;

    @NotNull(message = "MRP price is required")
    @Positive(message = "MRP must be greater than zero")
    private BigDecimal mrpPrice;

    @NotNull(message = "Selling price is required")
    @Positive(message = "Selling price must be greater than zero")
    private BigDecimal sellingPrice;

    @NotNull(message = "Available quantity is required")
    @PositiveOrZero(message = "Available quantity cannot be negative")
    private Integer availableQuantity;
}
