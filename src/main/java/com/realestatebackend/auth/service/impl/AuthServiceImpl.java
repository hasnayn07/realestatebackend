package com.realestatebackend.auth.service.impl;

import com.realestatebackend.auth.dto.*;
import com.realestatebackend.auth.entity.PasswordResetToken;
import com.realestatebackend.auth.entity.User;
import com.realestatebackend.auth.mapper.UserMapper;
import com.realestatebackend.auth.repository.PasswordResetTokenRepository;
import com.realestatebackend.auth.repository.UserRepository;
import com.realestatebackend.auth.service.*;
import com.realestatebackend.common.MailService;
import com.realestatebackend.common.exception.BadRequestException;
import com.realestatebackend.security.JwtService;
import com.realestatebackend.security.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepo;
    private final PasswordResetTokenRepository prRepo;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authManager;
    private final JwtService jwt;
    private final PasswordPolicyService policy;
    private final MailService mail;
    private final MfaService mfa;
    private final GoogleVerifierService googleVerifier;
    private final UserMapper mapper; // (kept for future mapping use)

    private AuthResponse tokensFor(User user) {
        String access = jwt.generate(Map.of("role", user.getRole().name()), user.getEmail(), false);
        String refresh = jwt.generate(Map.of("type", "refresh"), user.getEmail(), true);
        return AuthResponse.builder()
                .accessToken(access)
                .refreshToken(refresh)
                .tokenType("Bearer")
                .build();
    }

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest req, Role role) {
        if (userRepo.existsByEmail(req.getEmail())) {
            throw new BadRequestException("Email already in use");
        }
        policy.validate(req.getPassword(), req.getEmail());

        // DO NOT set the ID; Hibernate will generate it (@GeneratedValue + @UuidGenerator on entity)
        User user = User.builder()
                .email(req.getEmail())
                .password(encoder.encode(req.getPassword()))
                .fullName(req.getFullName())
                .role(role == null ? Role.CLIENT : role)
                .enabled(true)
                .build();

        userRepo.save(user); // INSERT with generated UUID
        return tokensFor(user);
    }

    @Override
    public AuthResponse login(AuthRequest req) {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
        );
        User user = userRepo.findByEmail(req.getEmail()).orElseThrow();
        if (Boolean.TRUE.equals(user.getMfaEnabled())) {
            // front-end must call /mfa/verify after this
            throw new BadCredentialsException("MFA_REQUIRED");
        }
        return tokensFor(user);
    }

    // --- TOTP MFA ---
    @Override
    @Transactional
    public String beginMfaSetup(String email) {
        User u = userRepo.findByEmail(email).orElseThrow();
        String secret = mfa.generateSecret();
        u.setMfaSecret(secret);
        userRepo.save(u);
        return mfa.otpauthUrl(secret, email);
    }

    @Override
    @Transactional
    public void enableMfa(String email, String code) {
        User u = userRepo.findByEmail(email).orElseThrow();
        if (u.getMfaSecret() == null || !mfa.verify(u.getMfaSecret(), code)) {
            throw new BadCredentialsException("Invalid OTP");
        }
        u.setMfaEnabled(true);
        userRepo.save(u);
    }

    @Override
    public AuthResponse verifyTotp(MfaVerifyRequest req) {
        User u = userRepo.findByEmail(req.getEmail()).orElseThrow();
        if (u.getMfaSecret() == null || !mfa.verify(u.getMfaSecret(), req.getCode())) {
            throw new BadCredentialsException("Invalid OTP");
        }
        return tokensFor(u);
    }

    // --- Email OTP MFA ---
    @Override
    @Transactional
    public void sendEmailOtp(String email) {
        User u = userRepo.findByEmail(email).orElseThrow();
        String otp = String.format("%06d", new java.util.Random().nextInt(1_000_000));
        u.setEmailOtp(otp);
        u.setEmailOtpExpiresAt(Instant.now().plusSeconds(300));
        userRepo.save(u);
        mail.send(u.getEmail(), "Your login OTP", "Code: " + otp + " (valid 5 minutes)");
    }

    @Override
    @Transactional
    public AuthResponse verifyEmailOtp(MfaVerifyRequest req) {
        User u = userRepo.findByEmail(req.getEmail()).orElseThrow();
        if (u.getEmailOtp() == null || u.getEmailOtpExpiresAt() == null
                || Instant.now().isAfter(u.getEmailOtpExpiresAt())
                || !u.getEmailOtp().equals(req.getCode())) {
            throw new BadCredentialsException("Invalid or expired OTP");
        }
        u.setEmailOtp(null);
        u.setEmailOtpExpiresAt(null);
        userRepo.save(u);
        return tokensFor(u);
    }

    // --- Password reset ---
    @Override
    @Transactional
    public void requestPasswordReset(String email) {
        // "Blind" response to avoid user enumeration: if user not found, do nothing but still return 200.
        userRepo.findByEmail(email).ifPresent(u -> {
            String token = UUID.randomUUID().toString().replace("-", "");
            PasswordResetToken pr = PasswordResetToken.builder()
                    // DO NOT set id; Hibernate generates it
                    .user(u)
                    .token(token)
                    .expiresAt(Instant.now().plusSeconds(3600))
                    .used(false)
                    .build();
            prRepo.save(pr);

            String link = "http://localhost:3000/reset-password?token=" + token; // your Next.js page
            mail.send(email, "Reset your password", "Click this link (valid 1 hour):\n" + link);
        });
        // Always return 200 from controller to avoid revealing whether the email exists
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest req) {
        PasswordResetToken pr = prRepo.findByToken(req.getToken())
                .orElseThrow(() -> new BadRequestException("Invalid token"));
        if (pr.isUsed() || Instant.now().isAfter(pr.getExpiresAt())) {
            throw new BadRequestException("Token expired");
        }

        User u = pr.getUser();
        policy.validate(req.getNewPassword(), u.getEmail());
        u.setPassword(encoder.encode(req.getNewPassword()));
        userRepo.save(u);

        pr.setUsed(true);
        prRepo.save(pr);
    }

    // --- Google Sign-In ---
    @Override
    @Transactional
    public AuthResponse loginWithGoogle(String idToken) {
        var payload = googleVerifier.verify(idToken);
        if (payload == null) {
            throw new BadCredentialsException("Invalid Google token");
        }
        String email = payload.getEmail();

        User user = userRepo.findByEmail(email).orElseGet(() -> {
            // New user from Google; DO NOT set id manually
            User nu = User.builder()
                    .email(email)
                    .fullName((String) payload.get("name"))
                    // set a random encoded password so the column is not null; user logs in via Google
                    .password(encoder.encode(UUID.randomUUID().toString()))
                    .role(Role.CLIENT)
                    .enabled(true)
                    .build();
            return userRepo.save(nu);
        });

        return tokensFor(user);
    }
}
