package com.luxora.service.impl;

import com.luxora.domain.ProductStatus;
import com.luxora.entity.Product;
import com.luxora.mapper.ProductMapper;
import com.luxora.repository.ProductRepository;
import com.luxora.response.ProductResponse;
import com.luxora.service.AdminProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminProductServiceImpl implements AdminProductService {

    private final ProductRepository productRepo;
    private final ProductMapper productMap;

    @Override
    public List<ProductResponse> getAllProducts() {
        return productRepo.findAll()
                .stream()
                .map(productMap::mapToResponse)
                .toList();
    }

    @Override
    public ProductResponse approveProduct(Long productId) {
        Product product = getProductOrThrow(productId);

        product.setStatus(ProductStatus.ACTIVE);
        product.setAdminRemarks("Approved by admin");
        product.setReviewedAt(LocalDateTime.now());

        Product saved = productRepo.save(product);
        log.info("Product Approved | id={}", product);

        return productMap.mapToResponse(saved);
    }

    @Override
    public ProductResponse blockProduct(Long productId, String reason) {
        if(reason == null || reason.isBlank()){
            throw new IllegalArgumentException("Block reason is required");
        }

        Product product = getProductOrThrow(productId);

        product.setStatus(ProductStatus.BLOCKED);
        product.setAdminRemarks(reason);
        product.setReviewedAt(LocalDateTime.now());

        Product saved = productRepo.save(product);
        log.info("Product Blocked | id={}", productId);

        return productMap.mapToResponse(saved);
    }

    private Product getProductOrThrow(Long productId) {
        return productRepo.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }
}
