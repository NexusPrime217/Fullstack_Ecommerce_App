package com.ecommerce.services;

import com.ecommerce.payloads.CategoryDTO;
import com.ecommerce.payloads.CategoryResponse;

public interface categoryService {
    CategoryResponse fetchAllCategory(Integer pageNumber, Integer pageSize,String sortBy, String sortOrder);

    CategoryDTO addCategory(CategoryDTO categoryDTO);

    CategoryDTO deleteCategoryById(Long categoryId);

    CategoryDTO updateCategory(CategoryDTO categoryDTO, Long categoryId);
}
