package com.ecommerce.payloads;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemDTO {
    private Long orderItemID;
    private ProductDTO product;
    private Integer quantity;
    private Double discount;
    private double orderedProductPrice;
}
