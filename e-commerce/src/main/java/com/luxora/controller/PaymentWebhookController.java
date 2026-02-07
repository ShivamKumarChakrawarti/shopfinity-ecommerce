package com.luxora.controller;

import com.luxora.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/webhooks/payments")
@RequiredArgsConstructor
public class PaymentWebhookController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<String> handleWebHook(
            @RequestBody String payload,
            @RequestHeader("X-Signature") String signature){

        paymentService.processWebhook(payload, signature);
        return ResponseEntity.ok("Webhook processed");
    }
}
