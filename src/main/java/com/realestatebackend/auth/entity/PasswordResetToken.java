package com.realestatebackend.auth.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

@Entity @Table(name="password_reset_token")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PasswordResetToken {
    @Id @UuidGenerator
    @JdbcTypeCode(SqlTypes.BINARY) @Column(columnDefinition="BINARY(16)")
    private UUID id;

    @ManyToOne(optional=false) @JoinColumn(name="user_id")
    private User user;

    @Column(nullable=false, unique=true, length=120) private String token;
    @Column(nullable=false) private Instant expiresAt;
    @Column(nullable=false) private boolean used = false;
}
