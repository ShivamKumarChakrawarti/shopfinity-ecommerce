package com.luxora.service.impl;

import com.luxora.dto.VariantCreateRequest;
import com.luxora.entity.Product;
import com.luxora.entity.ProductVariant;
import com.luxora.repository.ProductRepository;
import com.luxora.repository.ProductVariantRepository;
import com.luxora.request.ProductCreateRequest;
import com.luxora.service.SellerProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class SellerProductServiceImpl implements SellerProductService {

    private final ProductRepository productRepository;
    private final ProductVariantRepository variantRepository;

    @Override
    public Product createProduct(ProductCreateRequest request) {
        Product product = new Product();
        product.setTitle(request.getTitle());
        product.setDescription(request.getDescription());
        product.setActive(true);

        return productRepository.save(product);
    }

    @Override
    public ProductVariant addVariant(Long productId, VariantCreateRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(()-> new RuntimeException("product not found"));

        if(variantRepository.existBySku(request.getSku())){
            throw new RuntimeException("Sku already exists ");
        }

        if(request.getSellingPrice().compareTo(request.getMrpPrice()) > 0){
            throw new RuntimeException("Selling price can exceed mrp");
        }

        ProductVariant variant = new ProductVariant();
        variant.setProduct(product);
        variant.setSku(request.getSku());
        variant.setSize(request.getSize());
        variant.setColor(request.getColor());
        variant.setMrpPrice(request.getMrpPrice());
        variant.setSellingPrice(request.getSellingPrice());
        variant.setStockQuantity(request.getStockQuantity());
        variant.setActive(true);

        return variantRepository.save(variant);


    }
}
