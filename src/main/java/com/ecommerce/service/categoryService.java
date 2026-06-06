package com.ecommerce.service;

import com.ecommerce.model.Category;
import com.ecommerce.payload.CategoryDTO;
import com.ecommerce.payload.CategoryResponse;

import java.util.List;
import java.util.Locale;

public interface categoryService {
    CategoryResponse fetchAllCategory();

    CategoryDTO addCategory(CategoryDTO categoryDTO);

    CategoryDTO deleteCategoryById(Long categoryId);

    CategoryDTO updateCategory(CategoryDTO categoryDTO, Long categoryId);
}
