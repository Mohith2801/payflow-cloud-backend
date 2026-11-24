package com.mohith.payflow.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record CreatePaymentRequest(
    @NotBlank @Size(max = 80) String customerReference,
    @NotNull @DecimalMin(value = "0.01") @Digits(integer = 10, fraction = 2) BigDecimal amount,
    @NotBlank @Pattern(regexp = "[A-Z]{3}", message = "Currency must be a 3-letter uppercase code") String currency
) {}
