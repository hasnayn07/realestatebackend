package com.realestatebackend.booking.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "installments")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Installment {

    @Id
    @GeneratedValue
    @UuidGenerator
    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(columnDefinition = "BINARY(16)", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @Column(name = "installment_number", nullable = false)
    private Integer installmentNumber;      // 1, 2, 3, ...

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(name = "amount_due", nullable = false, precision = 14, scale = 2)
    private BigDecimal amountDue;

    @Column(name = "amount_paid", nullable = false, precision = 14, scale = 2)
    private BigDecimal amountPaid;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 12)
    private InstallmentStatus status;

    @PrePersist
    void prePersist() {
        if (amountPaid == null) amountPaid = BigDecimal.ZERO;
        if (status == null) status = InstallmentStatus.PENDING;
    }
}