package com.luxora.controller;

import com.luxora.response.ApiResponse;
import com.luxora.response.ProductResponse;
import com.luxora.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class CustomerProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> discoverProducts(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false, name = "q") String query) {

        return ResponseEntity.ok(
                new ApiResponse<>(
                        "Products fetched successfully",
                        true,
                        productService.discoverProducts(
                                page, size, sort, categoryId, query
                        )
                )
        );
    }
}
