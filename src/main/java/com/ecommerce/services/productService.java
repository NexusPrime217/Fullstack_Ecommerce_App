package com.ecommerce.services;

import com.ecommerce.models.Product;
import com.ecommerce.payloads.ProductDTO;
import com.ecommerce.payloads.ProductResponse;
import org.springframework.web.multipart.MultipartFile;


public interface productService {

    ProductDTO createProduct(ProductDTO productDTO, Long categoryId);

    ProductResponse getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    ProductResponse getProdByCat(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    ProductResponse searchProdByKeyword(String keyword, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    ProductDTO updateProduct(ProductDTO productDTO, Long productId);

    String deleteProduct(Long productId);

    ProductDTO updateProductImage(MultipartFile image, Long productId);
}
