package com.ecommerce.controllers;

import com.ecommerce.config.AppConstants;
import com.ecommerce.payloads.ProductDTO;
import com.ecommerce.payloads.ProductResponse;
import com.ecommerce.services.productService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class productController {

    @Autowired
    private productService productService;

    @PostMapping("/admin/categories/{CategoryId}/product")
    public ResponseEntity<ProductDTO> createProduct(@Valid @RequestBody ProductDTO productDTO, @PathVariable Long CategoryId){
        return new ResponseEntity<>(productService.createProduct(productDTO,CategoryId),HttpStatus.CREATED);
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

    @GetMapping("/public/categories/{categoryId}/products")
    public ResponseEntity<ProductResponse> getProductByCategory(
            @RequestParam(name="pageNumber", defaultValue = AppConstants.PAGE_NO) Integer pageNumber,
            @RequestParam(name="pageSize",defaultValue = AppConstants.PAGE_Size) Integer pageSize,
            @RequestParam(name="sortBy", defaultValue = AppConstants.SORT_CATEGORIES_BY) String sortBy,
            @RequestParam(name="sortOrder", defaultValue = AppConstants.SORT_DIR) String sortOrder,
            @PathVariable Long categoryId
    ){
        return new ResponseEntity<>(productService.getProdByCat(categoryId,pageNumber,pageSize,sortBy,sortOrder),HttpStatus.OK);
    }

    @GetMapping("/public/products/keyword/{keyword}")
    public ResponseEntity<ProductResponse> getProdByKeyword(
            @PathVariable String keyword,
            @RequestParam(name="pageNumber", defaultValue = AppConstants.PAGE_NO) Integer pageNumber,
            @RequestParam(name="pageSize", defaultValue = AppConstants.PAGE_Size) Integer pageSize,
            @RequestParam(name="sortBy", defaultValue = AppConstants.SORT_CATEGORIES_BY) String sortBy,
            @RequestParam(name="sortOrder", defaultValue = AppConstants.SORT_DIR) String sortOrder
    ){
        return new ResponseEntity<>(productService.searchProdByKeyword(keyword, pageNumber,pageSize,sortBy,sortOrder),HttpStatus.FOUND);
    }

    @PutMapping("/admin/products/{productId}")
    public ResponseEntity<ProductDTO> updateProduct(@RequestBody ProductDTO productDTO, @PathVariable Long productId){
        return new ResponseEntity<>(productService.updateProduct(productDTO,productId),HttpStatus.OK);
    }

    @DeleteMapping("/admin/products/{productId}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long productId){
        return new ResponseEntity<>(productService.deleteProduct(productId),HttpStatus.OK);
    }

    @PutMapping("/products/{productId}/image")
    public ResponseEntity<ProductDTO> updateProductImage(
            @PathVariable Long productId,
            @RequestParam(name="image")MultipartFile Image
    ){
        return new ResponseEntity<>(productService.updateProductImage(Image,productId),HttpStatus.OK);
    }
}
