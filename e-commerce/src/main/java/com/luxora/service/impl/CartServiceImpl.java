package com.luxora.service.impl;

import com.luxora.domain.ProductStatus;
import com.luxora.entity.*;
import com.luxora.repository.CartItemRepository;
import com.luxora.repository.CartRepository;
import com.luxora.repository.ProductRepository;
import com.luxora.repository.ProductVariantRepository;
import com.luxora.request.AddToCartRequest;
import com.luxora.request.UpdateCartItemRequest;
import com.luxora.response.CartItemResponse;
import com.luxora.response.CartResponse;
import com.luxora.service.CartService;
import com.luxora.service.UserServices;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserServices userServices;
    private final ProductVariantRepository productVariantRepository;

    @Override
    public CartResponse getCart(String jwt) throws Exception {
        User user = userServices.findUserByJwtToken(jwt);

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseGet(()-> createCartForUser(user));

        return mapToResponse(cart);
    }

    @Override
    public CartResponse addItemToCart(String jwt, AddToCartRequest request) throws Exception {
        User user = userServices.findUserByJwtToken(jwt);

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseGet(()-> createCartForUser(user));

        ProductVariant variant = productVariantRepository.findByIdAndActiveTrue(request.getVariantId())
                .orElseThrow(() -> new RuntimeException("Product variant not available"));

        if (variant.getStockQuantity() < request.getQuantity()) {
            throw new RuntimeException("Insufficient stock for selected variant");
        }

        CartItem cartItem  = cartItemRepository
                .findByCartIdAndProductId(cart.getId(), variant.getId())
                .orElse(null);

        if (cartItem == null) {
            // 6️⃣ Create new cart item
            cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setVariant(variant);
            cartItem.setQuantity(request.getQuantity());

            // 🔒 PRICE SNAPSHOT (CRITICAL)
            cartItem.setMrpPrice(variant.getMrpPrice());
            cartItem.setSellingPrice(variant.getSellingPrice());

        } else {
            // 7️⃣ Increase quantity
            int newQty = cartItem.getQuantity() + request.getQuantity();

            if (variant.getStockQuantity() < newQty) {
                throw new RuntimeException("Stock exceeded for selected variant");
            }

            cartItem.setQuantity(newQty);
        }

        // 8️⃣ Save changes
        cartItemRepository.save(cartItem);

        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);

        // 9️⃣ Return updated cart
        return mapToResponse(cart);
    }

    @Override
    public CartResponse updateItemQuantity(String jwt, Long productId, UpdateCartItemRequest request) throws Exception {
        User user = userServices.findUserByJwtToken(jwt);

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(()-> new RuntimeException("Cart not found"));

        CartItem cartItem = cartItemRepository
                .findByCartIdAndProductId(cart.getId(), productId)
                .orElseThrow(()-> new RuntimeException("Item not found"));

        if (request.getQuantity() == 0){
            cartItemRepository.delete(cartItem);
        }else{
            cartItem.setQuantity(request.getQuantity());
            cartItemRepository.save(cartItem);
        }
        cart.setUpdatedAt(LocalDateTime.now());
        return mapToResponse(cart);
    }

    @Override
    public CartResponse removeItem(String jwt, Long productId) throws Exception {
       User user = userServices.findUserByJwtToken(jwt);

       Cart cart = cartRepository.findByUserId(user.getId())
               .orElseThrow(()-> new RuntimeException("Cart not found"));

       CartItem item = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId)
               .orElseThrow(()-> new RuntimeException("Item not found"));

       cartItemRepository.delete(item);
       return mapToResponse(cart);
    }

    private Cart createCartForUser(User user){
        Cart cart = new Cart();
        cart.setUser(user);
        return cartRepository.save(cart);
    }

    private Product validateProductForCart(Long productId) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (product.getStatus() != ProductStatus.ACTIVE || product.getAvailableQuantity() <= 0) {
            throw new RuntimeException("Product not available");
        }
        return product;
    }

    private CartItem createCartItem(Cart cart, Product product){
        CartItem item = new CartItem();
        item.setCart(cart);
        item.setProduct(product);
        item.setQuantity(0);
        item.setMrpPrice(product.getMrpPrice());
        item.setSellingPrice(product.getSellingPrice());
        return item;
    }

    private CartResponse mapToResponse(Cart cart){
        BigDecimal totalMrp = BigDecimal.ZERO;
        BigDecimal totalSelling = BigDecimal.ZERO;

        List<CartItemResponse> items = new ArrayList<>();

        for(CartItem item : cart.getCartItems()){
            totalMrp = totalMrp.add(
                    item.getMrpPrice().multiply(BigDecimal.valueOf(item.getQuantity()))
            );

            totalSelling = totalSelling.add(
                    item.getSellingPrice().multiply(BigDecimal.valueOf(item.getQuantity()))
            );

            int discount = item.getMrpPrice()
                    .subtract(item.getSellingPrice())
                    .multiply(BigDecimal.valueOf(100))
                    .divide(item.getMrpPrice(), RoundingMode.HALF_UP)
                    .intValue();

            items.add(new CartItemResponse(
                    item.getProduct().getId(),
                    item.getProduct().getTitle(),
                    item.getMrpPrice(),
                    item.getSellingPrice(),
                    item.getQuantity(),
                    discount
            ));
        }

        return new CartResponse(
                items,
                totalMrp,
                totalSelling,
                totalMrp.subtract(totalSelling)
        );
    }
}
