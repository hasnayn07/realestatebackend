package com.realestatebackend.booking.service;

import com.realestatebackend.booking.dto.InstallmentResponse;

import java.util.List;
import java.util.UUID;

public interface InstallmentService {
    List<InstallmentResponse> getByBooking(UUID bookingId);
    List<InstallmentResponse> getOverdue();
}