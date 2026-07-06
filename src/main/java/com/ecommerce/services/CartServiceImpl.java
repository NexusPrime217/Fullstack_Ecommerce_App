package com.ecommerce.services;

import com.ecommerce.exceptions.APIException;
import com.ecommerce.exceptions.ResourceNotFoundException;
import com.ecommerce.models.Cart;
import com.ecommerce.models.CartItem;
import com.ecommerce.models.Product;
import com.ecommerce.models.User;
import com.ecommerce.payloads.CartDTO;
import com.ecommerce.payloads.ProductDTO;
import com.ecommerce.repositories.CartItemRepository;
import com.ecommerce.repositories.CartRepository;
import com.ecommerce.repositories.ProductRepository;
import com.ecommerce.util.AuthUtil;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class CartServiceImpl implements CartService{

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final AuthUtil authUtil;
    private final CartItemRepository cartItemRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public CartServiceImpl(CartRepository cartRepository, ProductRepository productRepository, AuthUtil authUtil, CartItemRepository cartItemRepository, ModelMapper modelMapper) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.authUtil = authUtil;
        this.cartItemRepository = cartItemRepository;
        this.modelMapper=modelMapper;
    }


    @Override
    public CartDTO addProductToCart(Long productId, Integer quantity) {
        //Find existing cart or create new one
        Cart cart = createCart();

        //Retrieve product details
        Product product = productRepository.findById(productId)
                .orElseThrow(()-> new ResourceNotFoundException("ProductId","Product",productId));

        //Perform validations
        CartItem cartItem= cartItemRepository.findCartItemByProductIdAndCartId(
                cart.getCartId(),
                productId);
        if (cartItem != null){
            throw new APIException("Product "+product.getProductName()+" already exist in the cart.");
        }

        if (product.getQuantity()==0){
            throw new APIException(product.getProductName()+" is not available");
        }

        if (product.getQuantity()<quantity){
            throw new APIException("Please, make an order of the "+product.getProductName()
            +"less than or equal to the quantity "+product.getQuantity()+".");
        }

        //Create cart item
        CartItem newCartItem = new CartItem();
        newCartItem.setProduct(product);
        newCartItem.setQuantity(quantity);
        newCartItem.setDiscount(product.getDiscount());
        newCartItem.setProductPrice(product.getSpecialPrice());
        newCartItem.setCart(cart);
        //save cart item
        cartItemRepository.save(newCartItem);

        //update cart and return
        product.setQuantity(product.getQuantity());
        cart.setTotalPrice(cart.getTotalPrice() + product.getSpecialPrice()*quantity);
        cartRepository.save(cart);

        CartDTO cartDTO=modelMapper.map(cart,CartDTO.class);

        List<CartItem> cartItems = cart.getCartItems();
        Stream<ProductDTO> productDTOStream = cartItems.stream().map(item->{
                    ProductDTO productDTO=modelMapper.map(item.getProduct(),ProductDTO.class);
                    productDTO.setQuantity(item.getQuantity());
                    return productDTO;
                });

        cartDTO.setProducts(productDTOStream.toList());

        return cartDTO;
    }

    @Override
    public List<CartDTO> getAllCarts() {
        List<Cart> carts=cartRepository.findAll();
        if (carts.isEmpty()){
            throw new APIException("No carts exist!");
        }
        List<CartDTO> cartDTOList=carts.stream()
                .map(cart-> {
                    CartDTO cartDTO=modelMapper.map(cart, CartDTO.class);
                    List<ProductDTO> productDTOList = cart.getCartItems().stream()
                            .map(item->{
                                ProductDTO productDTO = modelMapper.map(item.getProduct(),ProductDTO.class);
                                productDTO.setQuantity(item.getQuantity());
                                return productDTO;
                            }).toList();
                    cartDTO.setProducts(productDTOList);
                    return cartDTO;
                }).toList();
        return cartDTOList;
    }

    @Override
    public CartDTO getUserCart(User user) {
        Long userID=user.getUserId();
        Cart cart = cartRepository.findByCartUserId(user.getUserId())
                .orElseThrow(()->new ResourceNotFoundException("Cart","Username",user.getUsername()));

        CartDTO cartDTO = modelMapper.map(cart,CartDTO.class);

        List<ProductDTO> products = cart.getCartItems().stream()
                .map(item->{
                    ProductDTO product = modelMapper.map(item.getProduct(),ProductDTO.class);
                    product.setQuantity(item.getQuantity());
                    return product;
                })
                .toList();
        cartDTO.setProducts(products);
        return cartDTO;
    }

    @Transactional
    @Override
    public CartDTO updateProductQuantityInCart(Long productId, Integer quantity) {

        String userEmail=authUtil.loggedInEmail();
        Cart existingCart=cartRepository.findCartByEmail(userEmail);
        Long cartId=existingCart.getCartId();
        Cart cart=cartRepository.findById(cartId)
                .orElseThrow(()->new ResourceNotFoundException("Cart ID","Cart",cartId));

        Product product = productRepository.findById(productId)
                .orElseThrow(()-> new ResourceNotFoundException("ProductId","Product",productId));

        if (product.getQuantity()==0){
            throw new APIException(product.getProductName()+" is not available");
        }


        CartItem cartItem= cartItemRepository.findCartItemByProductIdAndCartId(cartId,productId);
        if (cartItem==null){
            throw new APIException("Product "+product.getProductName()+" not available in cart.");
        }

        if (product.getQuantity()<cartItem.getQuantity()+quantity){
            throw new APIException("Please, make an order of the "+product.getProductName()
                    +" less than or equal to the quantity "+product.getQuantity()+".");
        }
        cartItem.setProductPrice(product.getSpecialPrice());
        cartItem.setQuantity(cartItem.getQuantity()+quantity);
        cart.setTotalPrice(cart.getTotalPrice()+(cartItem.getProductPrice())*quantity);

        cartRepository.save(cart);
        CartItem updatedItem = cartItemRepository.save(cartItem);

        if (updatedItem.getQuantity()==0){
            cartItemRepository.deleteById(updatedItem.getCartItemId());
        }

        CartDTO cartDTO = modelMapper.map(cart,CartDTO.class);
        List<CartItem> cartItems = cart.getCartItems();
        List<ProductDTO> products = cartItems.stream()
                .map(item->{
                    ProductDTO productDTO = modelMapper.map(item.getProduct(),ProductDTO.class);
                    productDTO.setQuantity(item.getQuantity());
                    return productDTO;
                }).toList();
        cartDTO.setProducts(products);

        return cartDTO;
    }


    private Cart createCart(){
        Cart userCart= cartRepository.findCartByEmail(authUtil.loggedInEmail());
        if (userCart != null){
            return userCart;
        }

        Cart cart = new Cart();
        cart.setTotalPrice(0.0);
        cart.setUser(authUtil.loggedInUser());
        Cart newCart = cartRepository.save(cart);
        return newCart;
    }
}
