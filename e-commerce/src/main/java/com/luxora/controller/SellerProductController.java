package com.luxora.controller;

import com.luxora.request.ProductCreateRequest;
import com.luxora.response.ApiResponse;
import com.luxora.response.ProductResponse;
import com.luxora.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/seller/products")
@RequiredArgsConstructor
public class SellerProductController {

    private final ProductService productSvc;

    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(
            @RequestHeader("Authorization") String jwt,
            @Valid @RequestBody ProductCreateRequest request) throws Exception {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(
                        "Product created successfully",
                        true,
                        productSvc.createProduct(jwt, request)
                ));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getSellerProducts(
            @RequestHeader("Authorization") String jwt) throws Exception {

        return ResponseEntity.ok(
                new ApiResponse<>(
                        "Seller products fetched",
                        true,
                        productSvc.getSellerProducts(jwt)
                )
        );
    }
}
