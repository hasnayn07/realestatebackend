package com.realestatebackend.audit.entity;

public enum AuditAction {
    CREATE,
    UPDATE,
    DELETE,
    STATUS_CHANGE,
    PAYMENT_PROCESSED,
    PAYMENT_DEFAULTED,
    DOCUMENT_UPLOADED
}