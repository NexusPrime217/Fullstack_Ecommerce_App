package com.ecommerce.services;

import com.ecommerce.models.User;
import com.ecommerce.payloads.CartDTO;
import jakarta.transaction.Transactional;

import java.util.List;

public interface CartService {
    CartDTO addProductToCart(Long productId, Integer quantity);

    List<CartDTO> getAllCarts();

    CartDTO getUserCart(User user);

    @Transactional
    CartDTO updateProductQuantityInCart(Long productId, Integer quantity);
}
