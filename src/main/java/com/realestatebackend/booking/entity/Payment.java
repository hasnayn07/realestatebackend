package com.realestatebackend.booking.entity;

import com.realestatebackend.auth.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "payments")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue
    @UuidGenerator
    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(columnDefinition = "BINARY(16)", updatable = false, nullable = false)
    private UUID id;

    // The installment this payment is settling
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "installment_id", nullable = false)
    private Installment installment;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal amount;

    @Column(name = "paid_date", nullable = false)
    private LocalDate paidDate;

    // Uploaded bank-slip image (Option 1). Stored path/URL to the file.
    @Column(name = "proof_image_url", length = 500)
    private String proofImageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 12)
    private PaymentStatus status;

    // --- verification audit trail ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "verified_by")
    private User verifiedBy;

    @Column(name = "verified_at")
    private Instant verifiedAt;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @PrePersist
    void prePersist() {
        if (createdAt == null) createdAt = Instant.now();
        if (status == null) status = PaymentStatus.PENDING;     // claimed, awaiting review
    }
}