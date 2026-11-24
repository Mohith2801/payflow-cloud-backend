package com.mohith.payflow.dto;

import com.mohith.payflow.model.Payment;
import com.mohith.payflow.model.PaymentStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PaymentResponse(UUID id, String customerReference, BigDecimal amount,
                              String currency, PaymentStatus status, Instant createdAt) {
    public static PaymentResponse from(Payment p) {
        return new PaymentResponse(p.getId(), p.getCustomerReference(), p.getAmount(),
                p.getCurrency(), p.getStatus(), p.getCreatedAt());
    }
}
