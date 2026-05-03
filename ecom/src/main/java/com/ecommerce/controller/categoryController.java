package com.ecommerce.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ecommerce.project.model.Category;

import java.util.ArrayList;
import java.util.List;

@RestController
public class categoryController {

    private List<Category> categories=new ArrayList<>();

    @GetMapping("/api/public/categories")
    public List<Category> getAllCategories(){
        return categories;
    }
}
