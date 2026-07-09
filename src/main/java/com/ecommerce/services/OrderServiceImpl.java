package com.ecommerce.services;

import com.ecommerce.exceptions.APIException;
import com.ecommerce.exceptions.ResourceNotFoundException;
import com.ecommerce.models.*;
import com.ecommerce.payloads.OrderDTO;
import com.ecommerce.payloads.PaymentDTO;
import com.ecommerce.repositories.*;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class OrderServiceImpl implements OrderService{

    private final CartRepository cartRepository;
    private final AddressRepository addressRepository;
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final ModelMapper modelMapper;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final CartService cartService;

    @Autowired
    public OrderServiceImpl(CartRepository cartRepository, AddressRepository addressRepository, PaymentRepository paymentRepository, OrderRepository orderRepository, ModelMapper modelMapper, OrderItemRepository orderItemRepository, ProductRepository productRepository, CartService cartService) {
        this.cartRepository = cartRepository;
        this.addressRepository = addressRepository;
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.modelMapper = modelMapper;
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
        this.cartService = cartService;
    }

    @Override
    public OrderDTO placeOrder(String emailId, Long addressId, String paymentMethod, String pgName, String pgPaymentId, String pgResponseMessage, String pgStatus) {

        //Getting user cart
        Cart cart = cartRepository.findCartByEmail(emailId);

        if (cart == null){
            throw new ResourceNotFoundException("Cart","email",emailId);
        }

        Address address = addressRepository.findById(addressId)
                .orElseThrow(()->new ResourceNotFoundException("AddressId","Address",addressId));

        //Create a new order with payment information
        Order order = new Order();
        order.setEmail(emailId);
        order.setOrderDate(LocalDate.now());
        order.setTotalAmount(cart.getTotalPrice());
        order.setOrderStatus("Order Accepted!");
        order.setAddress(address);

        Payment payment = new Payment(paymentMethod, pgPaymentId, pgStatus, pgResponseMessage, pgName);
        payment.setOrder(order);
        Payment savedPayment = paymentRepository.save(payment);
        order.setPayment(savedPayment);

        PaymentDTO paymentDTO = modelMapper.map(savedPayment,PaymentDTO.class);

        Order savedOrder = orderRepository.save(order);

        //Get items from the cart into the order items
        List<CartItem> cartItems = cart.getCartItems();
        if (cartItems.isEmpty()){
            throw new APIException("Cart is empty");
        }
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cartItems){
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(savedOrder);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setDiscount(cartItem.getDiscount());
            orderItem.setOrderedProductPrice(cartItem.getProductPrice());
            orderItems.add(orderItem);
        }

        List<OrderItem> savedOrderItems = orderItemRepository.saveAll(orderItems);


//        order.setOrderItems(savedOrderItems);

        savedOrder.setOrderItems(savedOrderItems);

        //Update product stock
        cart.getCartItems().forEach(item->{
            int quantity = item.getQuantity();
            Product product = item.getProduct();
            product.setStockQuantity(product.getStockQuantity()-quantity);
            Product savedProduct = productRepository.save(product);

            //clear the cart
            cartService.deleteProdFromCart(cart.getCartId(),product.getProductId());
        });

        //Send back the order summary
        OrderDTO orderDTO = modelMapper.map(savedOrder,OrderDTO.class);
        orderDTO.setAddressId(addressId);
        orderDTO.setPaymentDTO(paymentDTO);
        return orderDTO;
    }
}
