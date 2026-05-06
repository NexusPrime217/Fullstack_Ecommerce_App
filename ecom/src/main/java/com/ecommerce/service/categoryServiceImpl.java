package com.ecommerce.service;

import com.ecommerce.project.model.Category;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class categoryServiceImpl implements categoryService{

    private final List<Category> categories=new ArrayList<>();
    private long NextId=1L;
    @Override
    public List<Category> fetchAllCategory() {
        return categories;
    }

    @Override
    public void addCategory(Category category) {
        category.setCategoryID(NextId++);
        categories.add(category);
    }
}
