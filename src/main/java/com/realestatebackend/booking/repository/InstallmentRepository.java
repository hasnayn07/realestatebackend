package com.realestatebackend.booking.repository;

import com.realestatebackend.booking.entity.Installment;
import com.realestatebackend.booking.entity.InstallmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface InstallmentRepository extends JpaRepository<Installment, UUID> {

    // A booking's full schedule, in order (1..N)
    List<Installment> findByBooking_IdOrderByInstallmentNumberAsc(UUID bookingId);

    // All installments currently in a given status
    List<Installment> findByStatus(InstallmentStatus status);

    /**
     * Installments that are past due and not yet fully paid.
     * Used both to flag OVERDUE and to drive the recovery dashboard.
     */
    @Query("""
           SELECT i FROM Installment i
           WHERE i.dueDate < :today
             AND i.status <> com.realestatebackend.booking.entity.InstallmentStatus.PAID
           """)
    List<Installment> findOverdue(@Param("today") LocalDate today);

    /**
     * Total outstanding (amountDue - amountPaid) across all unpaid installments
     * of one booking. COALESCE keeps it 0 instead of null when nothing matches.
     */
    @Query("""
           SELECT COALESCE(SUM(i.amountDue - i.amountPaid), 0)
           FROM Installment i
           WHERE i.booking.id = :bookingId
             AND i.status <> com.realestatebackend.booking.entity.InstallmentStatus.PAID
           """)
    BigDecimal totalOutstandingForBooking(@Param("bookingId") UUID bookingId);

    /**
     * System-wide outstanding total — the headline number on the recovery dashboard.
     */
    @Query("""
           SELECT COALESCE(SUM(i.amountDue - i.amountPaid), 0)
           FROM Installment i
           WHERE i.status <> com.realestatebackend.booking.entity.InstallmentStatus.PAID
           """)
    BigDecimal totalOutstandingOverall();
}