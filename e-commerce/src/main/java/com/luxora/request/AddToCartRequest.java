package com.luxora.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class AddToCartRequest {

    @NotNull
    private Long productId;

    @Positive
    private Integer quantity;
}
