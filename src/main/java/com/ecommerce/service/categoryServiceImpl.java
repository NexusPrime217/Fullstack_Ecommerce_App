package com.ecommerce.service;

import com.ecommerce.exception.APIException;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.model.Category;
import com.ecommerce.payload.CategoryDTO;
import com.ecommerce.payload.CategoryResponse;
import com.ecommerce.repository.categoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;

@Service
public class categoryServiceImpl implements categoryService{

    private final categoryRepository CategoryRepository;

    private final ModelMapper modelMapper;

    @Autowired
    public categoryServiceImpl(categoryRepository categoryRepository, ModelMapper modelMapper) {
        CategoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
    }



    @Override
    public CategoryResponse fetchAllCategory() {
        List<Category> categories=CategoryRepository.findAll();
        if (categories.isEmpty()){
            throw new APIException("There are no categories created yet!");
        }
        List<CategoryDTO> categoriesDTO=categories.stream()
                .map(category -> modelMapper.map(category, CategoryDTO.class))
                .toList();
        CategoryResponse categoryResponse=new CategoryResponse();
        categoryResponse.setContent(categoriesDTO);
        return categoryResponse;
    }

    @Override
    public CategoryDTO addCategory(CategoryDTO categoryDTO) {
        Category categoryFromDB=CategoryRepository.findByCategoryName(categoryDTO.getCategoryName());
        if (categoryFromDB!=null){
            throw new APIException("Category with Category Name '"+categoryDTO.getCategoryName()+"' already exists!!");
        }
        Category savedCategory=CategoryRepository.save(modelMapper.map(categoryDTO,Category.class));
        return modelMapper.map(savedCategory, CategoryDTO.class);
    }

    @Override
    public String deleteCategoryById(Long categoryId) {
        Optional<Category> category=CategoryRepository.findById(categoryId);
        if (category.isPresent()) {
            CategoryRepository.delete(category.get());
            return "Category " + categoryId + " deleted successfully!";
        }
        else{
            throw new ResourceNotFoundException("CategoryID","Category",categoryId);
        }

    }


    @Override
    public Category updateCategory(Category category,Long categoryId) {
        Optional<Category> savedCategoryOptional=CategoryRepository.findById(categoryId);
        if (savedCategoryOptional.isPresent()) {
            Category updatedCategory= savedCategoryOptional.get();
            updatedCategory.setCategoryName(category.getCategoryName());
            return CategoryRepository.save(updatedCategory);
        }
        else{
            throw new ResourceNotFoundException("CategoryID","Category",categoryId);
        }
    }



}
