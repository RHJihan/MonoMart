package com.monomart.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Value;
import lombok.Builder;

public class UserAddressDtos {

    // ----------------- RESPONSE DTO -----------------
    @Value
    @Builder
    public static class Response {
        Long id;
        String addressType;
        String addressLine1;
        String addressLine2;
        String upazila;
        String city;
        String country;
        String phone;
    }

    // ----------------- CREATE DTO -----------------
    @Data
    public static class CreateRequest {
        @NotBlank
        @Size(max = 20)
        private String addressType;

        @NotBlank
        @Size(max = 255)
        private String addressLine1;

        @Size(max = 255)
        private String addressLine2;

        @NotBlank
        @Size(max = 100)
        private String upazila;

        @NotBlank
        @Size(max = 100)
        private String city;

        @NotBlank
        @Size(max = 100)
        private String country;

        @NotBlank
        @Size(max = 20)
        private String phone;
    }

    // ----------------- UPDATE DTO -----------------
    @Data
    public static class UpdateRequest {
        @NotNull
        private Long id; // must be provided for updates

        @NotBlank
        @Size(max = 20)
        private String addressType;

        @NotBlank
        @Size(max = 255)
        private String addressLine1;

        @Size(max = 255)
        private String addressLine2;

        @NotBlank
        @Size(max = 100)
        private String upazila;

        @NotBlank
        @Size(max = 100)
        private String city;

        @NotBlank
        @Size(max = 100)
        private String country;

        @NotBlank
        @Size(max = 20)
        private String phone;
    }
}
