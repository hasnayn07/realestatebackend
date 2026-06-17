package com.realestatebackend.auth.service;

public interface MfaService {
    String generateSecret();
    String otpauthUrl(String secret, String email);
    boolean verify(String secret, String code);
}