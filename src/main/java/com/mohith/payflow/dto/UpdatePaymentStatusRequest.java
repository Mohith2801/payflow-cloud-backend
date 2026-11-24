package com.mohith.payflow.dto;

import com.mohith.payflow.model.PaymentStatus;
import jakarta.validation.constraints.NotNull;

public record UpdatePaymentStatusRequest(@NotNull PaymentStatus status) {}
