package com.luxora.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class Deal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)//This is used to generate value by the SpringBoot
    private Long id;

    private Integer discount;

    @OneToOne
    private HomeCategory category;
}
