package com.luxora.repository;

import com.luxora.domain.AccountStatus;
import com.luxora.entity.Seller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SellerRepository extends JpaRepository<Seller, Long> {
    Seller findByEmail(String email);
//    Optional<Seller> findByEmail(String email);

    boolean existsByEmail(String email);

    List<Seller> findByAccountStatus(AccountStatus status);

    @Query("""
           SELECT s FROM Seller s
           WHERE s.email = :email
           AND s.accountStatus <> 'DELETED'
           """)
    Optional<Seller> findActiveByEmail(@Param("email") String email);

    @Query("""
           SELECT s FROM Seller s
           WHERE s.accountStatus <> 'DELETED'
           """)
    List<Seller> findAllActive();

    long countByAccountStatus(AccountStatus status);

    // Page<Seller> findByAccountStatus(AccountStatus status, Pageable pageable);
    // List<Seller> findByBusinessNameContainingIgnoreCase(String keyword);
}
