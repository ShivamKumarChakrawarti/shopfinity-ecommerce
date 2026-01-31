package com.luxora.domain;

public enum ProductStatus {

    DRAFT,        // Seller created, not visible
    ACTIVE,       // Visible to customers
    OUT_OF_STOCK, // Auto-set when quantity = 0
    BLOCKED       // Admin blocked (trust/safety)
}