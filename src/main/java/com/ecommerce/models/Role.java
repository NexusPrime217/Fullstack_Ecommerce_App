package com.ecommerce.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.IdentityHashMap;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long roleId;

    @Enumerated(EnumType.STRING)
    @ToString.Exclude
    private AppRoles roleName;
}
