package com.realestatebackend.booking.controller;

import com.realestatebackend.auth.entity.User;
import com.realestatebackend.auth.repository.UserRepository;
import com.realestatebackend.booking.dto.PaymentResponse;
import com.realestatebackend.booking.dto.RecordPaymentRequest;
import com.realestatebackend.booking.service.PaymentService;
import com.realestatebackend.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService service;
    private final UserRepository userRepo;

    /**
     * Record a claimed payment with an uploaded proof image.
     * Consumes multipart/form-data: data fields + the file.
     *   POST /api/v1/payments  (form-data: installmentId, amount, paidDate, proof=<file>)
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','AGENT')")
    public ResponseEntity<PaymentResponse> record(
            @RequestParam UUID installmentId,
            @RequestParam BigDecimal amount,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate paidDate,
            @RequestPart("proof") MultipartFile proof) {

        RecordPaymentRequest req = new RecordPaymentRequest(installmentId, amount, paidDate);
        return ResponseEntity.ok(service.record(req, proof));
    }

    /** Manager confirms a pending payment; only now does it credit the installment. */
    @PatchMapping("/{id}/verify")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<PaymentResponse> verify(@PathVariable UUID id, Authentication auth) {
        return ResponseEntity.ok(service.verify(id, currentUserId(auth)));
    }

    /** Manager rejects a pending payment; balances untouched. */
    @PatchMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<PaymentResponse> reject(@PathVariable UUID id, Authentication auth) {
        return ResponseEntity.ok(service.reject(id, currentUserId(auth)));
    }

    // Resolve the logged-in user's UUID from the JWT principal (email).
    private UUID currentUserId(Authentication auth) {
        String email = auth.getName();
        User current = userRepo.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Authenticated user not found: " + email));
        return current.getId();
    }
}