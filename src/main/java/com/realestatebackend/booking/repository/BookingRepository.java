package com.realestatebackend.booking.repository;

import com.realestatebackend.booking.entity.Booking;
import com.realestatebackend.booking.entity.BookingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface BookingRepository extends JpaRepository<Booking, UUID> {

    Page<Booking> findByCustomer_Id(UUID customerId, Pageable pageable);

    List<Booking> findByStatus(BookingStatus status);

    // Guard: is this unit already tied up in an active booking?
    boolean existsByUnit_IdAndStatus(UUID unitId, BookingStatus status);

    long countByStatus(BookingStatus status);

    // Dealer Commission Aggregation
    @Query("""
        SELECT new com.realestatebackend.dealer.dto.DealerPayableDto(
            d.id, d.name, d.agencyName, 
            COUNT(b.id), 
            COALESCE(SUM(b.salePrice), 0)
        )
        FROM Booking b
        JOIN b.dealer d
        GROUP BY d.id, d.name, d.agencyName
    """)
    List<com.realestatebackend.dealer.dto.DealerPayableDto> getDealerSalesAggregates();

}
