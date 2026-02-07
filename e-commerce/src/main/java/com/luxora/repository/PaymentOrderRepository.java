package com.luxora.repository;

import com.luxora.entity.PaymentOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentOrderRepository extends JpaRepository<PaymentOrder, Long> {

    // Used during webhook callbacks
    Optional<PaymentOrder> findByGatewayPaymentOrderId(String gatewayPaymentOrderId);
}
