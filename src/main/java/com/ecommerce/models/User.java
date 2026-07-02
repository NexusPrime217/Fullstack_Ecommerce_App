package com.ecommerce.models;

import com.ecommerce.config.AppConstants;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="users",
        uniqueConstraints = {
        @UniqueConstraint(columnNames = "username"),
        @UniqueConstraint(columnNames = "email")
        })
public class User {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long userId;

    @Size(min= AppConstants.MIN_USERNAME,
            max=AppConstants.MAX_USERNAME,
            message = "Username length shall be between {min} and {max}"
    )
    private String username;

    @Email(message = "Invalid Email address")
    private String email;

    @Size(min= AppConstants.MIN_PASSWORD,
            max=AppConstants.MAX_PASSWORD,
            message = "Password length shall be between {min} and {max}"
    )
    private String password;

    @ManyToMany(cascade = CascadeType.ALL,
        fetch = FetchType.EAGER)
    @JoinTable(name="user_role",
        joinColumns = @JoinColumn(name="user_id"),
        inverseJoinColumns = @JoinColumn(name="role_id"))
    private Set<Role> roles=new HashSet<>();

    @ManyToMany(cascade = {CascadeType.PERSIST,CascadeType.MERGE})
    @JoinTable(name="user_address",
                joinColumns=@JoinColumn(name="user_id"),
                inverseJoinColumns = @JoinColumn(name="address_id"))
    private List<Address> addresses=new ArrayList<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "user",
                cascade = {CascadeType.MERGE,CascadeType.PERSIST},
                orphanRemoval = true)
    private Set<Product> products;

    public User(@NotBlank @Size(min=AppConstants.MIN_USERNAME, max= AppConstants.MAX_USERNAME,message = "Username must be between {min} and {max}.") String username,
                @NotBlank @Size(max=50) @Email String email,
                @NotBlank @Size(min=AppConstants.MIN_PASSWORD, max = AppConstants.MAX_PASSWORD,message = "Password must be between {min} and {max}")String password) {
        this.username=username;
        this.email=email;
        this.password=password;
    }
}
