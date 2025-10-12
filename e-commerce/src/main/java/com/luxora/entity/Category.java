package com.luxora.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;


@Entity
//@Table(name = "categories")
@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @NotNull
    @Column(unique=true)
    private String categoryId;

    @ManyToOne
//    @JoinColumn(name = "parent_category_id")
    private Category parentCategory;

    @NotNull
    private Integer level;

}
