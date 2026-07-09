package com.ecommerce.payloads;

import com.ecommerce.config.AppConstants;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {
    private Long productId;

    @NotBlank
    @Size(
            min= AppConstants.MIN_PRODUCT_NAME,
            max= AppConstants.MAX_PRODUCT_NAME,
            message="Minimum product name size should be between {min} and {max}"
    )
    private String productName;

    @NotBlank(message = "Product image should not be blank")
    private String image;

    @NotBlank
    @Size(
            min= AppConstants.MIN_PRODUCT_DESC,
            max= AppConstants.MAX_PRODUCT_DESC,
            message = "Minimum description size should be between {min} and {max}"
    )
    private String description;

    @NotNull
    @Min(
            value = AppConstants.MIN_QUANTITY,
            message = "Minimum Quantity should be {value}"
    )
    @Max(
            value=AppConstants.MAX_QUANTITY,
            message = "Maximum Quantity should be {value}"
    )
    private Integer stockQuantity;

    @NotNull
    @Min(
            value = 1,
            message = "Price should greater than {value}"
    )
    private Double price;

    @NotNull
    @Min(value = AppConstants.MIN_DISCOUNT,
            message = "Discount should be atleast {value}%"
    )
    @Max(value = AppConstants.MAX_DISCOUNT,
            message = "Discount cannot be greater than {value}%"
    )
    private Double discount;

    private Double specialPrice;

}
