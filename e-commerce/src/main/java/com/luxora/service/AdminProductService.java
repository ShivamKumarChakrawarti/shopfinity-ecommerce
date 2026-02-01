package com.luxora.service;

import com.luxora.response.ProductResponse;
import java.util.*;

public interface AdminProductService {

    List<ProductResponse> getAllProducts();

    ProductResponse approveProduct(Long productId);

    ProductResponse blockProduct(Long productId, String reason);
}
