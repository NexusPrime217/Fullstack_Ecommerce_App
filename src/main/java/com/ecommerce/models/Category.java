package com.ecommerce.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Entity(name="categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="category_ID")
    private Long categoryID;

    @NotBlank
    @Size(min=5,message="Minimum category name size should be 5")
    @Column(name="category_name")
    private String categoryName;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Product> products;
}
