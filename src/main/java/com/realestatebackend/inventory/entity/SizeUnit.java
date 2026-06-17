package com.realestatebackend.inventory.entity;

/**
 * Unit of measure for a Unit's size.
 * MARLA/KANAL are the standard Pakistani land units; SQFT for built area.
 * 1 Kanal = 20 Marla. We store the number + this unit rather than
 * normalizing, because developers quote sizes the way buyers expect them.
 */
public enum SizeUnit {
    MARLA,
    KANAL,
    SQFT
}