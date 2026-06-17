package com.realestatebackend.inventory.entity;

/**
 * The lifecycle of a Unit — the spine the whole CRM hangs off.
 * AVAILABLE       -> not yet sold, can be booked
 * BOOKED          -> reserved (token/down payment), schedule not yet active
 * ON_INSTALLMENTS -> active payment plan running
 * POSSESSION      -> fully paid / handed over
 * TRANSFERRED     -> ownership moved to another buyer (resale)
 * CANCELLED       -> booking fell through, unit returns to the pool
 */
public enum UnitStatus {
    AVAILABLE,
    BOOKED,
    ON_INSTALLMENTS,
    POSSESSION,
    TRANSFERRED,
    CANCELLED
}