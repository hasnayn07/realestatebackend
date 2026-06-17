package com.realestatebackend.auth.controller;

import com.realestatebackend.auth.dto.*;
import com.realestatebackend.auth.service.AuthService;
import com.realestatebackend.common.ApiResponse;
import com.realestatebackend.security.Role;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/v1/auth") @RequiredArgsConstructor
public class AuthController {
    private final AuthService service;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest req,
                                                 @RequestParam(defaultValue="CLIENT") Role role){
        return ResponseEntity.ok(service.register(req, role));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest req){
        return ResponseEntity.ok(service.login(req));
    }

    // MFA (TOTP)
    @PostMapping("/mfa/setup")
    public ResponseEntity<ApiResponse<String>> beginMfa(@RequestParam String email){
        return ResponseEntity.ok(ApiResponse.ok(service.beginMfaSetup(email))); // otpauth:// URI
    }
    @PostMapping("/mfa/enable")
    public ResponseEntity<Void> enableMfa(@RequestParam String email, @RequestParam String code){
        service.enableMfa(email, code); return ResponseEntity.ok().build();
    }
    @PostMapping("/mfa/verify")
    public ResponseEntity<AuthResponse> verifyTotp(@Valid @RequestBody MfaVerifyRequest req){
        return ResponseEntity.ok(service.verifyTotp(req));
    }

    // Email OTP
    @PostMapping("/email-otp/send")
    public ResponseEntity<Void> sendEmailOtp(@RequestParam String email){
        service.sendEmailOtp(email); return ResponseEntity.ok().build();
    }
    @PostMapping("/email-otp/verify")
    public ResponseEntity<AuthResponse> verifyEmailOtp(@Valid @RequestBody MfaVerifyRequest req){
        return ResponseEntity.ok(service.verifyEmailOtp(req));
    }

    // Password reset
    @PostMapping("/password/forgot")
    public ResponseEntity<Void> forgot(@RequestParam String email){
        service.requestPasswordReset(email); return ResponseEntity.ok().build();
    }
    @PostMapping("/password/reset")
    public ResponseEntity<Void> reset(@Valid @RequestBody ResetPasswordRequest req){
        service.resetPassword(req); return ResponseEntity.ok().build();
    }

    // Google Sign-In
    @PostMapping("/google")
    public ResponseEntity<AuthResponse> google(@RequestParam String idToken){
        return ResponseEntity.ok(service.loginWithGoogle(idToken));
    }
}
