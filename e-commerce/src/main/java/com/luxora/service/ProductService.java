package com.luxora.service;

import com.luxora.request.ProductCreateRequest;
import com.luxora.request.ProductUpdateRequest;
import com.luxora.response.ProductResponse;

import java.util.List;

public interface ProductService {
    ProductResponse createProduct(String jwt, ProductCreateRequest request) throws Exception;

    ProductResponse updateProduct(String jwt, Long productId, ProductUpdateRequest request) throws Exception;

    List<ProductResponse> getSellerProducts(String jwt) throws Exception;

    List<ProductResponse> getProductsForCustomers();
}
