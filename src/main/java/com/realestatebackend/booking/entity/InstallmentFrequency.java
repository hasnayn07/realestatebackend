package com.realestatebackend.booking.entity;

/**
 * How often installments fall due.
 * Each constant knows how many months it spans, so the schedule
 * generator can add the right interval without magic numbers.
 */
public enum InstallmentFrequency {
    MONTHLY(1),
    QUARTERLY(3);

    private final int months;

    InstallmentFrequency(int months) {
        this.months = months;
    }

    public int getMonths() {
        return months;
    }
}