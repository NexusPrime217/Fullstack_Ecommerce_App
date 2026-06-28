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
    private String buildingName;

    @NotBlank
    @Size(min=AppConstants.MIN_ADDRESS, message = "City name must be atleast {min} characters")
    private String cityName;

    @NotBlank
    @Size(min=2, message = "State name must be atleast {min} characters")
    private String stateName;

    @NotBlank
    @Size(min=2, message = "Country name must be atleast {min} characters")
    private String countryName;

    @NotBlank
    @Size(min=6, message = "pinCode must be atleast {min} characters")
    private String pinCode;

    @ManyToMany(mappedBy = "addresses")
    @ToString.Exclude
    private List<User> user;

    public Address(String street, String buildingName, String cityName, String stateName, String countryName, String pinCode) {
        this.street = street;
        this.buildingName = buildingName;
        this.cityName = cityName;
        this.stateName = stateName;
        this.countryName = countryName;
        this.pinCode = pinCode;
    }
}
