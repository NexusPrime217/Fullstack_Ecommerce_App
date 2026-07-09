package com.ecommerce.controllers;

import com.ecommerce.payloads.OrderDTO;
import com.ecommerce.payloads.OrderRequestDTO;
import com.ecommerce.services.OrderService;
import com.ecommerce.util.AuthUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class OrderController {

    private final OrderService orderService;
    private final AuthUtil authUtil;

    @Autowired
    public OrderController(OrderService orderService, AuthUtil authUtil) {
        this.orderService = orderService;
        this.authUtil = authUtil;
    }

    @PostMapping("/order/users/payments/{paymentMethod}")
    public ResponseEntity<OrderDTO> orderProducts(
            @PathVariable String paymentMethod,
            @RequestBody OrderRequestDTO orderRequestDTO
            ){
        String emailId = authUtil.loggedInEmail();
        OrderDTO orderDTO= orderService.placeOrder(
                emailId,
                orderRequestDTO.getAddressId(),
                paymentMethod,
                orderRequestDTO.getPgName(),
                orderRequestDTO.getPgPaymentId(),
                orderRequestDTO.getPgResponseMessage(),
                orderRequestDTO.getPgStatus()
        );
        return new ResponseEntity<>(orderDTO, HttpStatus.CREATED) ;
    }

}
