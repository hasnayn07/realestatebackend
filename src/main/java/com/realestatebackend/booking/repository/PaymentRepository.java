package com.realestatebackend.booking.repository;

import com.realestatebackend.booking.entity.Payment;
import com.realestatebackend.booking.entity.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    // All payments recorded against one installment
    List<Payment> findByInstallment_Id(UUID installmentId);

    // Queue of payments awaiting verification (status = PENDING), paged
    Page<Payment> findByStatus(PaymentStatus status, Pageable pageable);

    long countByStatus(PaymentStatus status);

    // 5. Collected this month (Only counting VERIFIED payments)
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.status = 'VERIFIED' " +
            "AND YEAR(p.verifiedAt) = :year AND MONTH(p.verifiedAt) = :month")
    BigDecimal getCollectedThisMonthAmount(@Param("year") int year, @Param("month") int month);

}