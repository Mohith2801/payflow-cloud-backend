package com.mohith.payflow.service;

import com.mohith.payflow.dto.CreatePaymentRequest;
import com.mohith.payflow.model.Payment;
import com.mohith.payflow.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {
    @Mock PaymentRepository repository;
    @InjectMocks PaymentService service;

    @Test void returnsExistingPaymentForRepeatedIdempotencyKey() {
        var request = new CreatePaymentRequest("customer-123", new BigDecimal("125.00"), "INR");
        Payment existing = new Payment("customer-123", new BigDecimal("125.00"), "INR", "retry-key-1");
        when(repository.findByIdempotencyKey("retry-key-1")).thenReturn(Optional.of(existing));
        assertSame(existing, service.create(request, "retry-key-1"));
        verify(repository, never()).save(any(Payment.class));
    }
}
