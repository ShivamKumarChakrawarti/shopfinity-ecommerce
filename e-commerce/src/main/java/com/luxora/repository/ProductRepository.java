package com.luxora.repository;

import com.luxora.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findBySellerId(Long sellerId);

    Optional<Product> findByIdAndSellerId(Long productId, Long sellerId);

    @Query("""
            SELECT c FROM cart_items c
            WHERE c.status = 'ACTIVE'
            AND c.availableQuantity > 0
            AND c.seller.accountStatus = 'ACTIVE'
            """)
    List<Product> findAllVisibleToCustomers();

    @Query("""
          SELECT p FROM Product p
          WHERE p.status = 'ACTIVE'
          AND p.availableQuantity > 0
          AND p.seller.accountStatus = 'ACTIVE'
          AND (
                :query IS NULL OR
                LOWER(p.title) LIKE LOWER(CONCAT('%', :query, '%'))
          )
    """)
        Page<Product> searchProducts(
               @Param("categoryId") Long categoryId,
               @Param("query") String query,
               Pageable pageable
        );
}
