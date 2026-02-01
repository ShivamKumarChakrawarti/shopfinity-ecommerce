package com.luxora.controller;

import com.luxora.response.ApiResponse;
import com.luxora.response.ProductResponse;
import com.luxora.service.AdminProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
public class AdminProductController {

    private final AdminProductService adminProductService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getAllProducts(){

        return ResponseEntity.ok(
                new ApiResponse<>(
                        "All product fetched",
                        true,
                        adminProductService.getAllProducts()
                )
        );

    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<ApiResponse<ProductResponse>> approveProduct(
            @PathVariable Long id){

        return ResponseEntity.ok(
                new ApiResponse<>(
                        "Product approved successfully",
                        true,
                        adminProductService.approveProduct(id)
                )
        );
    }

    public ResponseEntity<ApiResponse<ProductResponse>> blockProduct(
            @PathVariable Long id, @RequestParam String reason){

        return ResponseEntity.ok(
                new ApiResponse<>(
                        "Product bloced successfully",
                        true,
                        adminProductService.blockProduct(id, reason)
                )
        );
    }
}

