package com.luxora.controller;

import com.luxora.response.ApiResponse;
import com.luxora.response.ProductResponse;
import com.luxora.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class CustomerProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getProductsForCustomers() {

        return ResponseEntity.ok(
                new ApiResponse<>(
                        "Products fetched successfully",
                        true,
                        productService.getProductsForCustomers()
                )
        );
    }
}
