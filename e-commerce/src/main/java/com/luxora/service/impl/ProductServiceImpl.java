package com.luxora.service.impl;

import com.luxora.domain.AccountStatus;
import com.luxora.domain.ProductStatus;
import com.luxora.entity.Product;
import com.luxora.entity.Seller;
import com.luxora.mapper.ProductMapper;
import com.luxora.repository.ProductRepository;
import com.luxora.request.ProductCreateRequest;
import com.luxora.request.ProductUpdateRequest;
import com.luxora.response.ProductResponse;
import com.luxora.service.ProductService;
import com.luxora.service.SellerServices;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepo;
    private final SellerServices sellerSvc;
    private final ProductMapper productMapper;

    @Override
    public ProductResponse createProduct(String jwt, ProductCreateRequest req) throws Exception {
        Seller seller = sellerSvc.getSellerProfile(jwt);

        validateSellerCanSell(seller);
        validatePricing(req.getMrpPrice(), req.getSellingPrice());

        Product product = new Product();
        product.setTitle(req.getTitle());
        product.setDescription(req.getDescription());
        product.setMrpPrice(req.getMrpPrice());
        product.setSellingPrice(req.getSellingPrice());
        product.setAvailableQuantity(req.getAvailableQuantity());
        product.setSeller(seller);
        product.setStatus(ProductStatus.DRAFT);
        product.setCreatedAt(LocalDateTime.now());

        Product saved = productRepo.save(product);

        log.info("Product created | id={} | seller={}", saved.getId(), seller.getEmail());
        return productMapper.mapToResponse(saved);
    }


    @Override
    public ProductResponse updateProduct(String jwt, Long productId, ProductUpdateRequest request) throws Exception {

        Seller seller = sellerSvc.getSellerProfile(jwt);

        Product product = productRepo.findByIdAndSellerId(productId, seller.getId())
                .orElseThrow(()-> new RuntimeException("Product not found"));

        validatePricing(request.getMrpPrice(), request.getSellingPrice());

        product.setTitle(request.getTitle());
        product.setDescription(request.getDescription());
        product.setMrpPrice(request.getMrpPrice());
        product.setSellingPrice(request.getSellingPrice());
        product.setAvailableQuantity(request.getAvailableQuantity());
        product.setCreatedAt(LocalDateTime.now());

        if (product.getAvailableQuantity() == 0){
            product.setStatus(ProductStatus.OUT_OF_STOCK);
        }

        Product updated = productRepo.save(product);
        return productMapper.mapToResponse(updated);
    }

    // =========================
    // SELLER: VIEW OWN PRODUCTS
    // =========================
    @Override
    public List<ProductResponse> getSellerProducts(String jwt) throws Exception {

        Seller seller = sellerSvc.getSellerProfile(jwt);
        return productRepo.findBySellerId(seller.getId())
                .stream()
                .map(productMapper::mapToResponse)
                .toList();
    }

    @Override
    public List<ProductResponse> getProductsForCustomers() {
        return productRepo.findAllVisibleToCustomers()
                .stream()
                .map(this::mapToCustomerResponse)
                .toList();
    }

    @Override
    public Page<ProductResponse> discoverProducts(
            Integer page, Integer size, String sort, Long categoryId, String query){

        Pageable pageable = PageRequest.of(
                page != null ? page : 0,
                size != null ? size : 20,
                resolveSort(sort)
        );
        return productRepo.searchProducts(categoryId, query, pageable)
                .map(productMapper::mapToResponse);
    }

    // =========================
    // VALIDATIONS
    // =========================
    private void validateSellerCanSell(Seller seller) {
        if (seller.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new RuntimeException("Seller is not allowed to sell");
        }
    }

    private void validatePricing(BigDecimal mrp, BigDecimal selling) {

        if (mrp == null || selling == null) {
            throw new RuntimeException("Pricing fields are mandatory");
        }

        if (mrp.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("MRP must be greater than zero");
        }

        if (selling.compareTo(mrp) > 0) {
            throw new RuntimeException("Selling price cannot exceed MRP");
        }
    }

    private Sort resolveSort(String sort){
        if(sort == null) return Sort.by("createdAt").descending();

        return switch (sort){
            case "price_asc" -> Sort.by("sellingPrice").ascending();
            case "price_desc" -> Sort.by("sellingPrice").descending();
            case "discount" -> Sort.by("mrpPrice").descending();
            default -> Sort.by("createdAt").descending();
        };
    }

    private ProductResponse mapToCustomerResponse(Product product) {

        int discount = productMapper.calculateDiscountPercentage(product);

        return new ProductResponse(
                product.getId(),
                product.getTitle(),
                product.getDescription(),
                product.getMrpPrice(),
                product.getSellingPrice(),
                discount,
                product.getAvailableQuantity(),
                true,
                "Trusted Seller" // placeholder
        );
    }
}
