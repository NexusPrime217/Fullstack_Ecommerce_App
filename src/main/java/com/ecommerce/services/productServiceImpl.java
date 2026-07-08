package com.ecommerce.services;

import com.ecommerce.exceptions.APIException;
import com.ecommerce.exceptions.ResourceNotFoundException;
import com.ecommerce.models.Cart;
import com.ecommerce.models.Category;
import com.ecommerce.models.Product;
import com.ecommerce.payloads.CartDTO;
import com.ecommerce.payloads.ProductDTO;
import com.ecommerce.payloads.ProductResponse;
import com.ecommerce.repositories.CartRepository;
import com.ecommerce.repositories.CategoryRepository;
import com.ecommerce.repositories.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class productServiceImpl implements productService{

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private FileService fileService;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartService cartService;

    @Value("${project.image}")
    private String path;

    @Override
    public ProductDTO createProduct(ProductDTO productDTO, Long categoryId) {
        Category category=categoryRepository.findById(categoryId)
                .orElseThrow(()->new ResourceNotFoundException("Category ID","Category",categoryId));

        if (productRepository.existsByProductName(productDTO.getProductName())){
            throw new APIException(
                    "Product with ProductName: '"+productDTO.getProductName()+"' already exists."
            );
        }

        Product product=modelMapper.map(productDTO,Product.class);
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

    @Override
    public ProductResponse getProdByCat(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {

        Category category=categoryRepository.findById(categoryId)
                .orElseThrow(()->new ResourceNotFoundException("Category ID","Category",categoryId));

        Sort sortByAndOrder=sortOrder.equalsIgnoreCase("asc")
                 ?Sort.by(sortBy).ascending()
                 :Sort.by(sortBy).descending();

        Pageable pageDetails=PageRequest.of(pageNumber,pageSize,sortByAndOrder);
        Page<Product> productPage=productRepository.findByCategory(category,pageDetails);

        List<Product> productList=productPage.getContent();
        if (productList.isEmpty())
            throw new APIException("There are no products with category name: "+category.getCategoryName());
        List<ProductDTO> productDTOList=productList.stream()
                .map(product -> modelMapper.map(product,ProductDTO.class))
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

    @Override
    public ProductResponse searchProdByKeyword(String keyword, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ?Sort.by(sortBy).ascending()
                :Sort.by(sortBy).descending();

//        System.out.printf("PRODUCT SEARCH STARTED!!!");
        Pageable pageDetails = PageRequest.of(pageNumber,pageSize,sortByAndOrder);
        Page<Product> productPage = productRepository.findByProductNameLikeIgnoreCase('%'+keyword+'%',pageDetails);

        List<Product> productList = productPage.getContent();
        if (productList.isEmpty())
            throw new APIException("There are no products with keyword: "+keyword);
        List<ProductDTO> productDTOList=productList.stream()
                .map(product->modelMapper.map(product,ProductDTO.class))
                .toList();

        ProductResponse productResponse=new ProductResponse();
        //metadata
        productResponse.setPageNumber(pageNumber);
        productResponse.setPageSize(pageSize);
        productResponse.setTotalPages(productPage.getTotalPages());
        productResponse.setTotalElements(productPage.getNumberOfElements());
        productResponse.setLastPage(productPage.isLast());
        //content
        productResponse.setContent(productDTOList);
        return productResponse;
    }

    @Override
    public ProductDTO updateProduct(ProductDTO productDTO, Long productId) {
        Product productFromDB= productRepository.findById(productId)
                .orElseThrow(()->new ResourceNotFoundException("ProductId","Product",productId));


        productFromDB.setProductName(productDTO.getProductName());
        productFromDB.setDescription(productDTO.getDescription());
        productFromDB.setPrice(productDTO.getPrice());
        productFromDB.setDiscount(productDTO.getDiscount());
        productFromDB.setSpecialPrice(productDTO.getPrice()-(productDTO.getPrice()*productDTO.getDiscount()*0.01));
        productFromDB.setQuantity(productDTO.getQuantity());

        Product savedProduct=productRepository.save(productFromDB);

        List<Cart> carts=cartRepository.findCartsByProductId(productId);
        if (carts.isEmpty()){
            throw new APIException("No carts with productId "+productId+" Found!");
        }

        List<CartDTO> cartDTOs=carts.stream()
                .map(cart-> {
                    CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
                    List<ProductDTO> productDTOs=cart.getCartItems().stream()
                            .map(item-> modelMapper.map(item.getProduct(), ProductDTO.class)).toList();
                    cartDTO.setProducts(productDTOs);
                    return cartDTO;
                }).toList();

        cartDTOs.forEach(cart->cartService.updateProductInCarts(cart.getCartID(),productId));

        return modelMapper.map(savedProduct,ProductDTO.class);
    }

    @Override
    public String deleteProduct(Long productId) {
        Product productToDelete= productRepository.findById(productId)
                .orElseThrow(()->new ResourceNotFoundException("ProductId","Product",productId));

        List<Cart> carts=cartRepository.findCartsByProductId(productId);

        carts.forEach(cart->cartService.deleteProdFromCart(cart.getCartId(),productId));

        productRepository.delete(productToDelete);
        return "Product deleted successfully!";
    }

    @Override
    public ProductDTO updateProductImage(MultipartFile image, Long productId) {
        //Get product from the DB
        Product productFromDB= productRepository.findById(productId)
                .orElseThrow(()->new ResourceNotFoundException("ProductId","Product",productId));

        //Upload image to server
        //get the file name of uploaded image
        String fileName=fileService.uploadImage(path,image);

        //Updating the new file name to the product
        productFromDB.setImage(fileName);

        //save updated product
        Product savedProduct=productRepository.save(productFromDB);

        //return DTO after mapping the product to DTO
        return modelMapper.map(savedProduct,ProductDTO.class);
    }



}
