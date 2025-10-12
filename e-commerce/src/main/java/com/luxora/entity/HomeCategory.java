package com.luxora.entity;

import com.luxora.domain.HomeCategorySection;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class HomeCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)//This is used to generate value by the SpringBoot
    private long id;

    private String name;
    private String image;
    private String categoryId;
    private HomeCategorySection section;
}
