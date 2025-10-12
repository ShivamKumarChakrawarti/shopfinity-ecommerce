package com.luxora.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.*;
import lombok.*;


@Entity
@Table(name = "reviews")
@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(nullable = false)
    private String reviewText;

    @JoinColumn(nullable = false)
    private int rating;

    @ElementCollection
    private List<String> productImage;

    private String comment;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name="user_id", nullable = false)
    private User user;

    @JoinColumn(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
