package com.monomart.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "order_addresses")
public class OrderAddress extends BaseEntity {

    @NotBlank
    @Size(max = 20)
    @Column(name = "address_type", nullable = false, length = 20)
    private String addressType;

    @NotBlank
    @Size(max = 255)
    @Column(name = "address_line_1", nullable = false, length = 255)
    private String addressLine1;

    @Size(max = 255)
    @Column(name = "address_line_2", length = 255)
    private String addressLine2;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String upazila;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String city;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String country;

    @NotBlank
    @Size(max = 20)
    @Column(nullable = false, length = 20)
    private String phone;

    @Email
    @Size(max = 120)
    @Column(length = 120)
    private String email;
}
