package com.luxora.domain;

public enum OrderStatus {
    CREATED,        // order created, payment pending
    PAID,           // payment successful
    CANCELLED,      // user/admin cancelled
    FAILED
}
