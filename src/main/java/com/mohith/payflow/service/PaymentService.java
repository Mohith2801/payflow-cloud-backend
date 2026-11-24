package com.mohith.payflow.service;

import com.mohith.payflow.dto.CreatePaymentRequest;
import com.mohith.payflow.model.Payment;
import com.mohith.payflow.model.PaymentStatus;
import com.mohith.payflow.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import java.util.List;
import java.util.UUID;

@Service
public class PaymentService {
    private final PaymentRepository repository;
    public PaymentService(PaymentRepository repository) { this.repository = repository; }

    @Transactional
    public Payment create(CreatePaymentRequest request, String idempotencyKey) {
        if (idempotencyKey == null || idempotencyKey.isBlank() || idempotencyKey.length() > 100)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A valid Idempotency-Key header is required");
        return repository.findByIdempotencyKey(idempotencyKey).orElseGet(() ->
            repository.save(new Payment(request.customerReference(), request.amount(), request.currency(), idempotencyKey)));
    }
    @Transactional(readOnly = true)
    public Payment get(UUID id) {
        return repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment not found"));
    }
    @Transactional(readOnly = true)
    public List<Payment> list() { return repository.findAll(); }
    @Transactional
    public Payment updateStatus(UUID id, PaymentStatus status) {
        Payment payment = get(id);
        payment.setStatus(status);
        return repository.save(payment);
    }
}
