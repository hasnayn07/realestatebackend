package com.realestatebackend.booking.entity;

/**
 * State of a recorded payment in the verification workflow.
 * PENDING  -> claimed by an agent/buyer, proof uploaded, awaiting review
 * VERIFIED -> a manager confirmed it; ONLY now does it credit the installment
 * REJECTED -> proof didn't check out; does not affect balances
 */
public enum PaymentStatus {
    PENDING,
    VERIFIED,
    REJECTED
}