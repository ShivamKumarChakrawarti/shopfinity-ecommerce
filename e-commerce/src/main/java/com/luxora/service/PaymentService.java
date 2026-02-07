package com.luxora.service;

import com.luxora.response.PaymentOrderResponse;

public interface PaymentService {

    PaymentOrderResponse createPaymentOrder(String jwt, Long orderId) throws Exception;

    void processWebhook(String payload, String signature);
}
