package com.realestatebackend.auth.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class MfaVerifyRequest {
    @Email @NotBlank private String email;
    @NotBlank private String code;
}
