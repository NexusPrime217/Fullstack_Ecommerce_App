package com.ecommerce.controllers;

import com.ecommerce.config.AppConstants;
import com.ecommerce.models.Product;
import com.ecommerce.payloads.ProductDTO;
import com.ecommerce.payloads.ProductResponse;
import com.ecommerce.services.productService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class productController {

    @Autowired
    private productService productService;

    @PostMapping("/admin/categories/{CategoryId}/product")
    public ResponseEntity<ProductDTO> createProduct(@RequestBody Product product, @PathVariable Long CategoryId){
        return new ResponseEntity<>(productService.createProduct(product,CategoryId),HttpStatus.CREATED);
    }

    @GetMapping("/public/products")
    public ResponseEntity<ProductResponse> getAllProducts(
            @RequestParam(name="pageNumber", defaultValue = AppConstants.PAGE_NO) Integer pageNumber,
            @RequestParam(name="pageSize",defaultValue = AppConstants.PAGE_Size) Integer pageSize,
            @RequestParam(name="sortBy", defaultValue = AppConstants.SORT_CATEGORIES_BY) String sortBy,
            @RequestParam(name="sortOrder", defaultValue = AppConstants.SORT_DIR) String sortOrder
    ){
        return new ResponseEntity<>(productService.getAllProducts(pageNumber,pageSize,sortBy,sortOrder),HttpStatus.OK);
    }
}
