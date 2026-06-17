package com.realestatebackend.auth.dto;

import lombok.*;

@Data @Builder
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType;
}