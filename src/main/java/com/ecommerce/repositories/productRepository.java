package com.ecommerce.repositories;

import com.ecommerce.models.Category;
import com.ecommerce.models.Product;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.ecommerce.config.AppConstants;

@Repository
public interface productRepository extends JpaRepository<Product,Long> {
    Page<Product> findByCategory(Category category, Pageable pageDetails);

    Page<Product> findByProductNameLikeIgnoreCase(String keyword, Pageable pageDetails);

    Product findByProductName(@NotBlank @Size(
            min= AppConstants.MIN_PRODUCT_NAME,
            max= AppConstants.MAX_PRODUCT_NAME,
            message="Minimum product name size should be between {min} and {max}"
    ) String productName);


    boolean existsByProductName(@NotBlank @Size(
            min= AppConstants.MIN_PRODUCT_NAME,
            max= AppConstants.MAX_PRODUCT_NAME,
            message="Minimum product name size should be between {min} and {max}"
    ) String productName);
}
