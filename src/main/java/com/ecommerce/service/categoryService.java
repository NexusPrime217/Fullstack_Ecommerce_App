package com.ecommerce.service;

import com.ecommerce.project.model.Category;

import java.util.List;

public interface categoryService {
    List<Category> fetchAllCategory();

    void addCategory(Category category);

    String deleteCategoryById(Long categoryId);

    Category updateCategory(Category category, Long categoryId);
}
