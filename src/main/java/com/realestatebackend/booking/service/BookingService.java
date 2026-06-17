package com.realestatebackend.booking.service;

import com.realestatebackend.booking.dto.BookingResponse;
import com.realestatebackend.booking.dto.CreateBookingRequest;

import java.util.UUID;

public interface BookingService {
    BookingResponse create(CreateBookingRequest req);
    BookingResponse getById(UUID id);
    void cancel(UUID id);
}