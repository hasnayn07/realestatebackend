package com.realestatebackend.auth.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegisterRequest {
    @Email @NotBlank private String email;
    @NotBlank @Size(min=8, max=100) private String password;
    @NotBlank @Size(min=2, max=120) private String fullName;
}
