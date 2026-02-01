package com.luxora.request;

import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class UpdateCartItemRequest {

    @Positive
    private Integer quantity;
}
