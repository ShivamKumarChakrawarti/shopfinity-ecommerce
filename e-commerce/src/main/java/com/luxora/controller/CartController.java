package com.luxora.controller;

import com.luxora.request.AddToCartRequest;
import com.luxora.request.UpdateCartItemRequest;
import com.luxora.response.ApiResponse;
import com.luxora.response.CartResponse;
import com.luxora.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<ApiResponse<CartResponse>> getCart(
            @RequestHeader("Authorization") String jwt) throws Exception {

        return ResponseEntity.ok(
                new ApiResponse<>("Cart fetched successfully",
                true,
                        cartService.getCart(jwt))
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CartResponse>> addItem(
            @RequestHeader("Authorization") String jwt,
            @Valid @RequestBody AddToCartRequest request) throws Exception {

        return ResponseEntity.ok(
                new ApiResponse<>(
                        "Item added successfully",
                        true,
                        cartService.addItemToCart(jwt, request)
                )
        );
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ApiResponse<CartResponse>> updateItem(
            @RequestHeader("Authorization")String jwt, @PathVariable Long productId,
            @Valid @RequestBody UpdateCartItemRequest request) throws Exception {

        return ResponseEntity.ok(
                new ApiResponse<>("Cart updated successfully",
                        true,
                        cartService.updateItemQuantity(jwt, productId, request))
        );
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<ApiResponse<CartResponse>> removeItem(
            @RequestHeader("Authorization") String jwt,
            @PathVariable Long productId) throws Exception {

        return ResponseEntity.ok(
                new ApiResponse<>("Item removed from cart",
                        true,
                        cartService.removeItem(jwt, productId))
        );
    }
}
