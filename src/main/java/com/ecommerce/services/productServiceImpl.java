package com.ecommerce.services;

import com.ecommerce.exceptions.APIException;
import com.ecommerce.exceptions.ResourceNotFoundException;
import com.ecommerce.models.Category;
import com.ecommerce.models.Product;
import com.ecommerce.payloads.ProductDTO;
import com.ecommerce.payloads.ProductResponse;
import com.ecommerce.repositories.categoryRepository;
import com.ecommerce.repositories.productRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class productServiceImpl implements productService{

    @Autowired
    private productRepository productRepository;

    @Autowired
    private categoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ProductDTO createProduct(Product product, Long categoryId) {
        Category category=categoryRepository.findById(categoryId)
                .orElseThrow(()->new ResourceNotFoundException("Category ID","Category",categoryId));

        product.setCategory(category);
        //Calculate Special price: Price after discount.
        Double specialPrice=product.getPrice()-(product.getPrice()*(product.getDiscount())*0.01);
        product.setSpecialPrice(specialPrice);
        Product savedProduct=productRepository.save(product);
        return modelMapper.map(savedProduct, ProductDTO.class);
    }

    @Override
    public ProductResponse getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder=sortOrder.equalsIgnoreCase("asc")
                ?Sort.by(sortBy).ascending()
                :Sort.by(sortBy).descending();

        Pageable pageDetails= PageRequest.of(pageNumber,pageSize,sortByAndOrder);
        Page<Product> productPage=productRepository.findAll(pageDetails);

        List<Product> productList=productPage.getContent();
        if (productList.isEmpty())
            throw new APIException("There are no products here");
        List<ProductDTO> productDTOList=productList.stream().
                map(product -> modelMapper.map(product,ProductDTO.class))
                .toList();

        ProductResponse productResponse=new ProductResponse();
        //Set Metadata
        productResponse.setPageNumber(pageNumber);
        productResponse.setPageSize(pageSize);
        productResponse.setTotalPages(productPage.getTotalPages());
        productResponse.setTotalElements(productPage.getNumberOfElements());
        productResponse.setLastPage(productPage.isLast());

        //Set Content
        productResponse.setContent(productDTOList);

        return productResponse;
    }
}
