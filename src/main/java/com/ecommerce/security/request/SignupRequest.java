package com.ecommerce.security.request;

import com.ecommerce.config.AppConstants;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class SignupRequest {

    @NotBlank
    @Size(min=AppConstants.MIN_USERNAME, max= AppConstants.MAX_USERNAME,message = "Username must be between {min} and {max}.")
    private String username;

    @NotBlank
    @Size(max=50)
    @Email
    private String email;

    private Set<String> role;

    @NotBlank
    @Size(min=AppConstants.MIN_PASSWORD, max = AppConstants.MAX_PASSWORD,message = "Password must be between {min} and {max}")
    private String password;

}
