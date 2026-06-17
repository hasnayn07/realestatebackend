package com.realestatebackend.auth.entity;

import com.realestatebackend.security.Role;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue
    @UuidGenerator
    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(columnDefinition = "BINARY(16)", updatable = false, nullable = false)
    private UUID id;


    @Column(nullable = false, unique = true, length = 190)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private Role role;

    @Column(nullable = false)
    private boolean enabled = true;

    // TOTP MFA
    private String mfaSecret;
    private Boolean mfaEnabled = false;

    // Email OTP
    private String emailOtp;
    private Instant emailOtpExpiresAt;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    @PrePersist
    void prePersist() {
        if (createdAt == null) createdAt = Instant.now();
        if (mfaEnabled == null) mfaEnabled = false; // avoid null in DB
    }
}
