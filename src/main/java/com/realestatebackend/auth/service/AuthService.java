package com.realestatebackend.auth.service;

import com.realestatebackend.auth.dto.*;
import com.realestatebackend.security.Role;

public interface AuthService {
    AuthResponse register(RegisterRequest req, Role role);
    AuthResponse login(AuthRequest req);

    // MFA (TOTP + Email)
    String beginMfaSetup(String email);      // returns otpauth:// URI
    void enableMfa(String email, String code);
    AuthResponse verifyTotp(MfaVerifyRequest req);
    void sendEmailOtp(String email);
    AuthResponse verifyEmailOtp(MfaVerifyRequest req);

    // Password reset
    void requestPasswordReset(String email);
    void resetPassword(ResetPasswordRequest req);

    // Google Sign-In
    AuthResponse loginWithGoogle(String idToken);
}
