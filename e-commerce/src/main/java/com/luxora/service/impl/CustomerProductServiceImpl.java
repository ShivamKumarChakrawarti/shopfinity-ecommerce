package com.luxora.service.impl;

import com.luxora.entity.Product;
import com.luxora.entity.ProductVariant;
import com.luxora.repository.ProductRepository;
import com.luxora.repository.ProductVariantRepository;
import com.luxora.service.CustomerProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerProductServiceImpl implements CustomerProductService {

    private final ProductRepository productRepository;
    private final ProductVariantRepository variantRepository;

    @Override
    public Product getProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    @Override
    public List<ProductVariant> getVariants(Long productId) {
        return variantRepository.findByProductIdAndActiveTrue(productId);
    }
}
