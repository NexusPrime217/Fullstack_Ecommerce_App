package com.ecommerce.models;

import com.ecommerce.config.AppConstants;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "order_item")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderItemId;

    @ManyToOne
    private Product product;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @NotNull
    @Min(
            value = AppConstants.MIN_QUANTITY,
            message = "Minimum Quantity should be {value}"
    )
    @Max(
            value=AppConstants.MAX_QUANTITY,
            message = "Maximum Quantity should be {value}"
    )
    private Integer quantity;

    @NotNull
    @Min(value = AppConstants.MIN_DISCOUNT,
            message = "Discount should be atleast {value}%"
    )
    @Max(value = AppConstants.MAX_DISCOUNT,
            message = "Discount cannot be greater than {value}%"
    )
    private Double discount;

    private Double orderedProductPrice;
}
