package com.realestatebackend.booking.repository;

import com.realestatebackend.booking.entity.Installment;
import com.realestatebackend.booking.entity.InstallmentStatus;
import com.realestatebackend.recovery.dto.DefaulterDto;
import org.springframework.data.domain.Pageable;
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

    // 1. Total strictly overdue
    @Query("SELECT COALESCE(SUM(i.amountDue - i.amountPaid), 0) FROM Installment i WHERE i.status = 'OVERDUE'")
    BigDecimal getTotalOverdueAmount();

    // 2. Due this month
    @Query("SELECT COALESCE(SUM(i.amountDue - i.amountPaid), 0) FROM Installment i WHERE i.status IN ('PENDING', 'OVERDUE') " +
            "AND YEAR(i.dueDate) = :year AND MONTH(i.dueDate) = :month")
    BigDecimal getDueThisMonthAmount(@Param("year") int year, @Param("month") int month);

    // 3. The Defaulters Projection
    // Note: If Customer entity uses 'customerName' or Block uses 'blockName', adjust the fields (c.fullName, b.name) below.
    @Query("""
        SELECT new com.realestatebackend.recovery.dto.DefaulterDto(
            c.id, c.fullName, c.phone, 
            u.id, u.unitNumber, b.name, 
            CAST(COUNT(i) AS int), 
            SUM(i.amountDue - i.amountPaid)
        )
        FROM Installment i
        JOIN i.booking bk
        JOIN bk.customer c
        JOIN bk.unit u
        JOIN u.block b
        WHERE i.status = 'OVERDUE'
        GROUP BY c.id, c.fullName, c.phone, u.id, u.unitNumber, b.name
        ORDER BY SUM(i.amountDue - i.amountPaid) DESC
    """)
    List<DefaulterDto> findTopDefaulters(Pageable pageable);

}