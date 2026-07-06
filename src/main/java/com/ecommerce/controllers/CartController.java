package com.ecommerce.controllers;

import com.ecommerce.models.Cart;
import com.ecommerce.models.User;
import com.ecommerce.payloads.CartDTO;
import com.ecommerce.services.CartService;
import com.ecommerce.util.AuthUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CartController {

    private final CartService cartService;
    private final AuthUtil authUtil;

    @Autowired
    public CartController(CartService cartService, AuthUtil authUtil) {
        this.cartService = cartService;
        this.authUtil = authUtil;
    }

    @PostMapping("/carts/products/{productId}/quantity/{quantity}")
    public ResponseEntity<CartDTO> addProductToCart(
            @PathVariable Long productId,
            @PathVariable Integer quantity
    ){
        CartDTO cartDTO=cartService.addProductToCart(productId,quantity);
        return new ResponseEntity<>(cartDTO, HttpStatus.CREATED);
    }

    @GetMapping("/carts")
    private ResponseEntity<List<CartDTO>> getAllCarts() {
        List<CartDTO> carts=cartService.getAllCarts();
        return new ResponseEntity<>(carts,HttpStatus.OK);
    }

    @GetMapping("/carts/users/cart")
    private ResponseEntity<CartDTO> getUserCart(){
        User user = authUtil.loggedInUser();
        CartDTO cart=cartService.getUserCart(user);
        return new ResponseEntity<>(cart,HttpStatus.OK);
    }

    @PutMapping("/cart/products/{productId}/quantity/{operation}")
    private ResponseEntity<CartDTO> updateQuantity(
            @PathVariable Long productId,
            @PathVariable String operation
    ){
        CartDTO cartDTO = cartService.updateProductQuantityInCart(productId,
                operation.equalsIgnoreCase("delete")?-1:1);

        return new ResponseEntity<>(cartDTO,HttpStatus.OK);
    }
}
