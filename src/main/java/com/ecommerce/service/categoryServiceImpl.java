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
        Optional<Category> category=CategoryRepository.findById(categoryId);
        if (category.isPresent()) {
            CategoryRepository.delete(category.get());
            return "Category " + categoryId + " deleted succesfully!";
        }
        else{
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Category not found");
        }

    }


    @Override
    public Category updateCategory(Category category,Long categoryId) {
        Optional<Category> savedCategoryOptional=CategoryRepository.findById(categoryId);

        Category savedCategory=savedCategoryOptional.orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Category not found"));

//        Optional<Category> optionalCategory=categories.stream()
//                .filter(cat->cat.getCategoryID().equals(categoryId))
//                .findFirst();
        category.setCategoryName(category.getCategoryName());
        return CategoryRepository.save(category);
    }



}
