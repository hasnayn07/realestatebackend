package com.realestatebackend.dealer.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record DealerRequest(
        @NotBlank(message = "Dealer name is required")
        String name,

        @NotBlank(message = "CNIC is required")
        @Pattern(regexp = "^[0-9]{13}$", message = "CNIC must be exactly 13 digits without dashes")
        String cnic,

        String agencyName,

        @NotNull(message = "Commission percentage is required")
        @DecimalMin(value = "0.0", inclusive = true, message = "Commission cannot be negative")
        @Digits(integer = 3, fraction = 2, message = "Invalid commission format")
        BigDecimal commissionPercentage
) {
}