package com.luxora.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SellerReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)//This is used to generate value by the SpringBoot
    private long id;

    @OneToOne
    private Seller seller;

    private Long totalEarnings = 0L;

    private Long totalSales=0L;

    private Long totalRefunds = 0L;

    private Long totalTax = 0L;

    private Long netEarnings = 0L;

    private Integer totalOrders = 0;

    private Integer cancledOrders = 0;

    private Integer totalTransactions = 0;
}
