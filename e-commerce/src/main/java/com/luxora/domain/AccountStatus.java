package com.luxora.domain;

public enum AccountStatus {
    PENDING_VERIFICATION, //Account is created but not yet verified
    VERIFIED,
    ACTIVE,      // Account is active and can perform all operations
    INACTIVE,    // Account is inactive (e.g., needs verification)
    SUSPENDED,   // Account is temporarily suspended due to policy violations
    DEACTIVATED, // Account is deactivated, user may deactivate it
    BANNED,      // Account is permanently banned, due to sever violation
    CLOSED,       // Account is permanently closed, possibly at user request
    DELETED
}
