package com.ecommerce.services;

import com.ecommerce.payloads.OrderDTO;

public interface OrderService {
    OrderDTO placeOrder(String emailId, Long addressId, String paymentMethod, String pgName, String pgPaymentId, String pgResponseMessage, String pgStatus);
}
