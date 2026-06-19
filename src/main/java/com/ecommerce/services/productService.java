package com.ecommerce.services;

import com.ecommerce.models.Product;
import com.ecommerce.payloads.ProductDTO;
import com.ecommerce.payloads.ProductResponse;


public interface productService {

    ProductDTO createProduct(Product product, Long categoryId);

    ProductResponse getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);
}
