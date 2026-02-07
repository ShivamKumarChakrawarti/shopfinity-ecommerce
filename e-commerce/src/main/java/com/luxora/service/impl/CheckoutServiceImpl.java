package com.luxora.service.impl;

import com.luxora.domain.OrderStatus;
import com.luxora.entity.*;
import com.luxora.repository.*;
import com.luxora.request.CheckoutRequest;
import com.luxora.response.OrderResponse;
import com.luxora.service.CheckoutService;
import com.luxora.service.UserServices;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CheckoutServiceImpl implements CheckoutService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderRepository orderRepository;
    private final ProductVariantRepository productVariantRepository;
    private final UserServices userServices;
    private AddressRepository addressRepository;

    @Transactional
    @Override
    public OrderResponse checkout(String jwt, CheckoutRequest request) throws Exception {
        User user = userServices.findUserByJwtToken(jwt);

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Cart is empty"));

        if (cart.getCartItems().isEmpty()){
            throw new RuntimeException("Cart is empty");
        }

        Address address = addressRepository.findById(request.getAddressId())
                .orElseThrow(()-> new RuntimeException("Address not found"));

        BigDecimal totalMrp = BigDecimal.ZERO;
        BigDecimal totalSelling = BigDecimal.ZERO;

        Order order = new Order();
        order.setUser(user);
        order.setShippingAddress(address);
        order.setOrderStatus(OrderStatus.CREATED);
        order.setOrderNumber(UUID.randomUUID().toString());
        order.setCreatedAt(LocalDateTime.now());

        for(CartItem cartItem : cart.getCartItems()){
            ProductVariant variant = productVariantRepository.findById(cartItem.getVariant().getId())
                    .orElseThrow(() -> new RuntimeException("Variant not found"));

            if (variant.getStockQuantity() < cartItem.getQuantity()) {
                throw new RuntimeException(
                        "Insufficient stock for SKU: " + variant.getSku()
                );
            }

            variant.setStockQuantity(
                    variant.getStockQuantity() - cartItem.getQuantity()
            );
            productVariantRepository.save(variant);

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setVariant(variant);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setMrpPrice(cartItem.getMrpPrice());
            orderItem.setSellingPrice(cartItem.getSellingPrice());

            order.getOrderItems().add(orderItem);

            totalMrp = totalMrp.add(
                    cartItem.getMrpPrice()
                            .multiply(BigDecimal.valueOf(cartItem.getQuantity()))
            );

            totalSelling = totalSelling.add(
                    cartItem.getSellingPrice()
                            .multiply(BigDecimal.valueOf(cartItem.getQuantity()))
            );
        }

        order.setTotalMrpPrice(totalMrp);
        order.setTotalSellingPrice(totalSelling);
        order.setTotalDiscount(totalMrp.subtract(totalSelling));

        Order savedOrder = orderRepository.save(order);

        cartItemRepository.deleteAll(cart.getCartItems());
        cart.getCartItems().clear();
        cartRepository.save(cart);

        log.info("Order created | orderNumber={} | user={}",
                savedOrder.getOrderNumber(), user.getEmail());

        return new OrderResponse(
                savedOrder.getId(),
                savedOrder.getOrderNumber(),
                savedOrder.getOrderStatus(),
                savedOrder.getTotalMrpPrice(),
                savedOrder.getTotalSellingPrice(),
                savedOrder.getTotalDiscount(),
                savedOrder.getCreatedAt()
        );
    }
}
