package com.realestatebackend.booking.entity;

/**
 * State of one installment.
 * PENDING -> due in future (or today), nothing/insufficient paid, not yet overdue
 * PARTIAL -> some money received but less than amountDue
 * PAID    -> fully settled (amountPaid >= amountDue)
 * OVERDUE -> past dueDate and still not fully paid (the recovery target)
 */
public enum InstallmentStatus {
    PENDING,
    PARTIAL,
    PAID,
    OVERDUE
}