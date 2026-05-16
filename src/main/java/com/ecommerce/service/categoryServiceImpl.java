package com.ecommerce.service;

import com.ecommerce.project.model.Category;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    @Override
    public String deleteCategoryById(Long categoryId) {
        Category category=categories.stream()
        .filter(cat -> cat.getCategoryID().equals(categoryId))
                .findFirst().orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Category not found!"));
        categories.remove(category);
        return "Category "+categoryId+" deleted succesfully!";
    }


    @Override
    public Category updateCategory(Category category,Long categoryId) {
        Optional<Category> optionalCategory=categories.stream()
                .filter(cat->cat.getCategoryID().equals(categoryId))
                .findFirst();
        if (optionalCategory.isPresent()){
            Category currentCategory=optionalCategory.get();
            currentCategory.setCategoryName(category.getCategoryName());
//            return "Category "+categoryId+" removed.";
            return currentCategory;
        }
        else{
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Category not found");
        }
    }


}
