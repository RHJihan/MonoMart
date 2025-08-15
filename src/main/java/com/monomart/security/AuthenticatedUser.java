package com.monomart.security;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthenticatedUser implements Serializable {
    private final Long userId;
    private final String username;
    private final String role; // e.g., ROLE_ADMIN or ROLE_CUSTOMER
}


