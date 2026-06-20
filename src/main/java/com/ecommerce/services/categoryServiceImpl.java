package com.ecommerce.services;

import com.ecommerce.exceptions.APIException;
import com.ecommerce.exceptions.ResourceNotFoundException;
import com.ecommerce.models.Category;
import com.ecommerce.payloads.CategoryDTO;
import com.ecommerce.payloads.CategoryResponse;
import com.ecommerce.repositories.categoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    public CategoryResponse fetchAllCategory(Integer pageNumber, Integer pageSize,String sortBy, String sortOrder) {
        Sort sortByAndOrder=sortOrder.equalsIgnoreCase("asc")
                ?Sort.by(sortBy).ascending()
                :Sort.by(sortBy).descending();

        Pageable pageDetails= PageRequest.of(pageNumber,pageSize,sortByAndOrder);
        Page<Category> categoryPage=CategoryRepository.findAll(pageDetails);

        List<Category> categories=categoryPage.getContent();
        if (categories.isEmpty()){
            throw new APIException("There are no categories created yet!");
        }
        List<CategoryDTO> categoriesDTO=categories.stream()
                .map(category -> modelMapper.map(category, CategoryDTO.class))
                .toList();

        CategoryResponse categoryResponse=new CategoryResponse();
        categoryResponse.setContent(categoriesDTO);
        categoryResponse.setPageNumber(categoryPage.getNumber());
        categoryResponse.setPageSize(categoryPage.getSize());
        categoryResponse.setTotalElements(categoryPage.getNumberOfElements());
        categoryResponse.setTotalPages(categoryPage.getTotalPages());
        categoryResponse.setLastPage(categoryPage.isLast());

        return categoryResponse;
    }

    @Override
    public CategoryDTO addCategory(CategoryDTO categoryDTO) {
        boolean isCategoryPresent=CategoryRepository.existsByCategoryName(categoryDTO.getCategoryName());
        if (isCategoryPresent){
            throw new APIException("Category with Category Name '"+categoryDTO.getCategoryName()+"' already exists!!");
        }
        Category savedCategory=CategoryRepository.save(modelMapper.map(categoryDTO,Category.class));
        return modelMapper.map(savedCategory, CategoryDTO.class);
    }

    @Override
    public CategoryDTO deleteCategoryById(Long categoryId) {
        Optional<Category> category=CategoryRepository.findById(categoryId);
        if (category.isPresent()) {
            Category deletedCategory=category.get();
            CategoryRepository.delete(deletedCategory);
            return modelMapper.map(deletedCategory, CategoryDTO.class);
        }
        else{
            throw new ResourceNotFoundException("CategoryID","Category",categoryId);
        }
    }


    @Override
    public CategoryDTO updateCategory(CategoryDTO category,Long categoryId) {
        Optional<Category> savedCategoryOptional=CategoryRepository.findById(categoryId);
        if (savedCategoryOptional.isPresent()) {
            Category updatedCategory= savedCategoryOptional.get();
            updatedCategory.setCategoryName(category.getCategoryName());
            Category savedCategory=CategoryRepository.save(updatedCategory);
            return modelMapper.map(savedCategory,CategoryDTO.class);
        }
        else{
            throw new ResourceNotFoundException("CategoryID","Category",categoryId);
        }
    }



}
