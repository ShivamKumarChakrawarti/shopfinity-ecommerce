package com.luxora.service;

import com.luxora.request.AddToCartRequest;
import com.luxora.request.UpdateCartItemRequest;
import com.luxora.response.CartResponse;

public interface CartService {

    CartResponse getCart(String jwt) throws Exception;

    CartResponse addItemToCart(String jwt, AddToCartRequest request) throws Exception;

    CartResponse updateItemQuantity(
            String jwt,
            Long productId,
            UpdateCartItemRequest request) throws Exception;

    CartResponse removeItem(String jwt, Long productId) throws Exception;
}
