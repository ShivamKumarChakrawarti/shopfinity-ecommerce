package com.luxora.service.impl;

import com.luxora.domain.AccountStatus;
import com.luxora.domain.ProductStatus;
import com.luxora.entity.Product;
import com.luxora.entity.Seller;
import com.luxora.repository.ProductRepository;
import com.luxora.request.ProductCreateRequest;
import com.luxora.request.ProductUpdateRequest;
import com.luxora.response.ProductResponse;
import com.luxora.service.ProductService;
import com.luxora.service.SellerServices;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepo;
    private final SellerServices sellerSvc;

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
        return mapToResponse(saved);
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
        return mapToResponse(updated);
    }

    // =========================
    // SELLER: VIEW OWN PRODUCTS
    // =========================
    @Override
    public List<ProductResponse> getSellerProducts(String jwt) throws Exception {

        Seller seller = sellerSvc.getSellerProfile(jwt);
        return productRepo.findBySellerId(seller.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<ProductResponse> getProductsForCustomers() {
        return productRepo.findAllVisibleToCustomers()
                .stream()
                .map(this::mapToCustomerResponse)
                .toList();
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

    private ProductResponse mapToResponse(Product product) {

        int discount = calculateDiscount(product);

        return new ProductResponse(
                product.getId(),
                product.getTitle(),
                product.getDescription(),
                product.getMrpPrice(),
                product.getSellingPrice(),
                discount,
                product.getAvailableQuantity(),
                product.getAvailableQuantity() > 0,
                null // seller trust added later
        );
    }

    private ProductResponse mapToCustomerResponse(Product product) {

        int discount = calculateDiscount(product);

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

    private int calculateDiscount(Product product) {

        return product.getMrpPrice()
                .subtract(product.getSellingPrice())
                .multiply(BigDecimal.valueOf(100))
                .divide(product.getMrpPrice(), RoundingMode.HALF_UP)
                .intValue();
    }

}
