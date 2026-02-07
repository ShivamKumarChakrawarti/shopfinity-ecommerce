package com.luxora.service;

import com.luxora.dto.VariantCreateRequest;
import com.luxora.entity.Product;
import com.luxora.entity.ProductVariant;
import com.luxora.request.ProductCreateRequest;

public interface SellerProductService {

    Product createProduct(ProductCreateRequest request);

    ProductVariant addVariant(
            Long productId,
            VariantCreateRequest request
    );
}
