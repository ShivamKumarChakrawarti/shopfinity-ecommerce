package com.luxora.service;

import com.luxora.entity.Product;
import com.luxora.entity.ProductVariant;

import java.util.List;

public interface CustomerProductService {

    Product getProduct(Long productId);

    List<ProductVariant> getVariants(Long productId);
}
