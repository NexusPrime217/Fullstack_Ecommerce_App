package com.ecommerce.models;

import com.ecommerce.config.AppConstants;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.AnyDiscriminatorImplicitValues;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long AddressId;

    @NotBlank
    @Size(min= AppConstants.MIN_ADDRESS, message = "Street name must be atleast {min} characters")
    private String street;

    @NotBlank
    @Size(min=AppConstants.MIN_ADDRESS, message = "Building name must be atleast {min} characters")
    private String building;

    @NotBlank
    @Size(min=AppConstants.MIN_ADDRESS, message = "City name must be atleast {min} characters")
    private String city;

    @NotBlank
    @Size(min=2, message = "State name must be atleast {min} characters")
    private String state;

    @NotBlank
    @Size(min=2, message = "Country name must be atleast {min} characters")
    private String country;

    @NotBlank
    @Size(min=4, message = "pinCode must be atleast {min} characters")
    private String pinCode;

    @ManyToOne
    @ToString.Exclude
    @JoinColumn(name = "user_id")
    private User user;

    public Address(String street, String building, String city, String state, String country, String pinCode) {
        this.street = street;
        this.building = building;
        this.city = city;
        this.state = state;
        this.country = country;
        this.pinCode = pinCode;
    }
}
