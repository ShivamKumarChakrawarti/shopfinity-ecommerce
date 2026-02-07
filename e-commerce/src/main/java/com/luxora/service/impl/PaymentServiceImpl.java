package com.luxora.service.impl;

import com.luxora.config.RazorpayConfig;
import com.luxora.domain.OrderStatus;
import com.luxora.domain.PaymentMethod;
import com.luxora.domain.PaymentStatus;
import com.luxora.entity.Order;
import com.luxora.entity.PaymentOrder;
import com.luxora.entity.User;
import com.luxora.repository.OrderRepository;
import com.luxora.repository.PaymentOrderRepository;
import com.luxora.response.PaymentOrderResponse;
import com.luxora.service.PaymentService;
import com.luxora.service.UserServices;
import com.razorpay.RazorpayClient;
import com.razorpay.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final OrderRepository orderRepository;
    private final PaymentOrderRepository paymentOrderRepository;
    private final UserServices userService;
    private final RazorpayClient razorpayClient;
    private final RazorpayConfig razorpayConfig;

    private String getWebhookSecret() {
        return razorpayConfig.getWebhookSecret();
    }

    @Override
    @Transactional
    public PaymentOrderResponse createPaymentOrder(String jwt, Long orderId) throws Exception {
        User user = userService.findUserByJwtToken(jwt);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("Unauthorized order access");
        }

        if (!order.getOrderStatus().equals(OrderStatus.CREATED)) {
            throw new RuntimeException("Order already processed");
        }

        JSONObject options = new JSONObject();
        options.put("amount", order.getTotalSellingPrice().multiply(BigDecimal.valueOf(100))); // paise
        options.put("currency", "INR");
        options.put("receipt", "order_rcpt_" + order.getId());

        com.razorpay.Order rzpOrder = razorpayClient.orders.create(options);

        PaymentOrder paymentOrder = new PaymentOrder();
        paymentOrder.setOrders(List.of(order));
        paymentOrder.setAmount(order.getTotalSellingPrice());
        paymentOrder.setStatus(PaymentStatus.PROCESSING);
        paymentOrder.setPaymentMethod(PaymentMethod.RAZORPAY);
        paymentOrder.setGatewayPaymentOrderId(rzpOrder.get("id"));
        paymentOrder.setCreatedAt(LocalDateTime.now());

        paymentOrderRepository.save(paymentOrder);

        log.info("Payment order created | paymentOrderId={}, gatewayOrderId={}",
                paymentOrder.getId(), rzpOrder.get("id"));



        return new PaymentOrderResponse(
                paymentOrder.getId(),
                rzpOrder.get("id"),
                paymentOrder.getAmount(),
                PaymentMethod.RAZORPAY
        );
    }

    @Override
    @Transactional
    public void processWebhook(String payload, String signature){
        if (!verifySignature(payload, signature)) {
            throw new SecurityException("Invalid webhook signature");
        }

        JSONObject json = new JSONObject(payload);
        String event = json.getString("event");

        JSONObject entity = json.getJSONObject("payload")
                .getJSONObject("payment")
                .getJSONObject("entity");

        String gatewayOrderId = entity.getString("order_id");
        String paymentStatus = entity.getString("status");

        PaymentOrder paymentOrder = paymentOrderRepository.findByGatewayPaymentOrderId(gatewayOrderId)
                .orElseThrow(() -> new RuntimeException("Payment order not found"));

        if (paymentOrder.getStatus() == PaymentStatus.SUCCESS) {
            log.info("Payment already processed | gatewayOrderId={}", gatewayOrderId);
            return;
        }

        if("Captured".equals(paymentStatus)){
            paymentOrder.setStatus(PaymentStatus.SUCCESS);

            for(Order order : paymentOrder.getOrders()){
                order.setOrderStatus(OrderStatus.PAID);
                orderRepository.save(order);
            }
            log.info("Payment SUCCESS | gatewayOrderId={}", gatewayOrderId);

        }else {
            paymentOrder.setStatus(PaymentStatus.FAILED);
            for (Order order : paymentOrder.getOrders()){
                order.setOrderStatus(OrderStatus.FAILED);
                orderRepository.save(order);
            }
            log.warn("Payment FAILED | gatewayOrderId={}", gatewayOrderId);
        }

        paymentOrderRepository.save(paymentOrder);
    }

    private boolean verifySignature(String payload, String signature) {
        // DEV MODE BYPASS
        if ("DEV_MODE".equals(signature)) {
            return true;
        }

        try {
            Utils.verifyWebhookSignature(
                    payload,
                    signature,
                    razorpayConfig.getWebhookSecret()
            );
            return true;
        } catch (Exception e) {
            log.error("Webhook signature verification failed", e);
            return false;
        }
    }
}
