package com.realestatebackend.booking.controller;

import com.realestatebackend.booking.dto.InstallmentResponse;
import com.realestatebackend.booking.service.InstallmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/installments")
@RequiredArgsConstructor
public class InstallmentController {

    private final InstallmentService service;

    @GetMapping("/by-booking/{bookingId}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','AGENT')")
    public ResponseEntity<List<InstallmentResponse>> byBooking(@PathVariable UUID bookingId) {
        return ResponseEntity.ok(service.getByBooking(bookingId));
    }

    // recovery: everything past due and not fully paid
    @GetMapping("/overdue")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<List<InstallmentResponse>> overdue() {
        return ResponseEntity.ok(service.getOverdue());
    }
}