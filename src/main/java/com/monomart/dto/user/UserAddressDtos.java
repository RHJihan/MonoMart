package com.monomart.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserAddressDtos {

    // ----------------- RESPONSE DTO -----------------
    @Getter
    @Setter
    public static class Response {
        private Long id;
        private String addressType;
        private String addressLine1;
        private String addressLine2;
        private String upazila;
        private String city;
        private String country;
        private String phone;
    }

    // ----------------- CREATE DTO -----------------
    @Getter
    @Setter
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
    @Getter
    @Setter
    public static class UpdateRequest {
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
