package com.ecommerce.repository;

import com.ecommerce.model.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;

public interface categoryRepository extends JpaRepository<Category,Long> {
    Category findByCategoryName(@NotBlank  @Size(min=5,message="Minimum category name size should be 5") String categoryName);
}
