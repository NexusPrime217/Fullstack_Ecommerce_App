package com.ecommerce.service;

import com.ecommerce.model.Category;
import com.ecommerce.repository.categoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class categoryServiceImpl implements categoryService{


    @Autowired
    private categoryRepository CategoryRepository;


    @Override
    public List<Category> fetchAllCategory() {
        return CategoryRepository.findAll();
    }

    @Override
    public void addCategory(Category category) {
          CategoryRepository.save(category);
    }

    @Override
    public String deleteCategoryById(Long categoryId) {
        List<Category> categories=CategoryRepository.findAll();

        Category category=categories.stream()
        .filter(cat -> cat.getCategoryID().equals(categoryId))
                .findFirst().orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Category not found!"));
        CategoryRepository.delete(category);
        return "Category "+categoryId+" deleted succesfully!";
    }


    @Override
    public Category updateCategory(Category category,Long categoryId) {
        List<Category> categories=CategoryRepository.findAll();

        Optional<Category> optionalCategory=categories.stream()
                .filter(cat->cat.getCategoryID().equals(categoryId))
                .findFirst();
        if (optionalCategory.isPresent()){
            Category currentCategory=optionalCategory.get();
            currentCategory.setCategoryName(category.getCategoryName());
            return CategoryRepository.save(currentCategory);
        }
        else{
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Category not found");
        }
    }


}
