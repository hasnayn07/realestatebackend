package com.realestatebackend.booking.service;

import com.realestatebackend.booking.dto.PaymentResponse;
import com.realestatebackend.booking.dto.RecordPaymentRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface PaymentService {
    // record a claimed payment (status PENDING) with an uploaded proof image
    PaymentResponse record(RecordPaymentRequest req, MultipartFile proof);

    // a manager confirms it; ONLY now does the money credit the installment
    PaymentResponse verify(UUID paymentId, UUID verifierUserId);

    // reject a claimed payment (does not affect balances)
    PaymentResponse reject(UUID paymentId, UUID verifierUserId);
}