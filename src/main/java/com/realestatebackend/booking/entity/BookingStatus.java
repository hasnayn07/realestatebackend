package com.realestatebackend.booking.entity;

/**
 * Lifecycle of a booking.
 * ACTIVE    -> created, installments in progress
 * COMPLETED -> all installments fully paid
 * CANCELLED -> booking fell through; unit returns to the pool
 */
public enum BookingStatus {
    ACTIVE,
    COMPLETED,
    CANCELLED
}