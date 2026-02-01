package com.luxora.controller;

import com.luxora.request.CheckoutRequest;
import com.luxora.response.ApiResponse;
import com.luxora.response.OrderResponse;
import com.luxora.service.CheckoutService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/checkout")
@RequiredArgsConstructor
public class CheckoutController {

    private final CheckoutService checkoutService;

    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> checkout(
            @RequestHeader("Authorization") String jwt,
            @Valid @RequestBody CheckoutRequest request) throws Exception {

        return ResponseEntity.ok(
                new ApiResponse<>("Order created", true,
                        checkoutService.checkout(jwt, request))
        );
    }
}
