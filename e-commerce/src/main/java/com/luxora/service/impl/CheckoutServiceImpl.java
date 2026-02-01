package com.luxora.service.impl;

import com.luxora.domain.OrderStatus;
import com.luxora.entity.*;
import com.luxora.repository.AddressRepository;
import com.luxora.repository.CartRepository;
import com.luxora.repository.OrderRepository;
import com.luxora.repository.ProductRepository;
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
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
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
        order.setOrderStatus(OrderStatus.CREATED);
        order.setCreatedAt(LocalDateTime.now());
        order.setOrderNumber(UUID.randomUUID().toString());
        order.setShippingAddress(address);

        for(CartItem cartItem : cart.getCartItems()){
            Product product = productRepository.findById(cartItem.getProduct().getId())
                    .orElseThrow(()-> new RuntimeException("Product not found"));

            if (product.getAvailableQuantity() < cartItem.getQuantity()){
                throw new RuntimeException(
                        "Insufficient stock for product: " + product.getTitle()
                );
            }

            product.setAvailableQuantity(product.getAvailableQuantity() - cartItem.getQuantity());
            productRepository.save(product);

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
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
