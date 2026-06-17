package com.realestatebackend.booking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCustomerRequest(

        @NotBlank(message = "Full name is required")
        @Size(max = 150)
        String fullName,

        @NotBlank(message = "CNIC is required")
        @Size(max = 20, message = "CNIC must be at most 20 characters")
        String cnic,

        @Size(max = 20)
        String phone,

        @Size(max = 300)
        String address
) {}