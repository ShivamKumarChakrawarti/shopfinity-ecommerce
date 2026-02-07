package com.luxora.controller;

import com.luxora.response.ApiResponse;
import com.luxora.response.PaymentOrderResponse;
import com.luxora.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/create/{orderId}")
    public ResponseEntity<ApiResponse<PaymentOrderResponse>> createPayment(
            @RequestHeader("Authorization") String jwt,
            @PathVariable Long orderId) throws Exception {

        return ResponseEntity.ok(
                new ApiResponse<>(
                        "Payment order created successfully",
                        true,
                        paymentService.createPaymentOrder(jwt, orderId)
                )
        );
    }
}
