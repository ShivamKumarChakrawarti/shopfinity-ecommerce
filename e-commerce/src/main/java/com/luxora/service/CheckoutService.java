package com.luxora.service;

import com.luxora.request.CheckoutRequest;
import com.luxora.response.OrderResponse;

public interface CheckoutService {
    OrderResponse checkout(String jwt, CheckoutRequest request) throws Exception;
}
