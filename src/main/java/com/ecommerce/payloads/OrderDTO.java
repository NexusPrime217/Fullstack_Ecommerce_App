package com.ecommerce.payloads;

import com.ecommerce.models.Address;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {

    private Long orderId;
    private String email;
    private List<OrderItemDTO> orderItems = new ArrayList<>();
    private PaymentDTO paymentDTO;
    private LocalDate orderDate;
    private Double totalAmount;
    private String orderStatus;
    private Long addressId;

}
