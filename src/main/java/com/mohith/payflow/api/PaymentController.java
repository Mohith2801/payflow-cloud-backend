package com.mohith.payflow.api;

import com.mohith.payflow.dto.*;
import com.mohith.payflow.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    private final PaymentService service;
    public PaymentController(PaymentService service) { this.service = service; }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentResponse create(@Valid @RequestBody CreatePaymentRequest request,
                                  @RequestHeader(value = "Idempotency-Key", required = false) String key) {
        return PaymentResponse.from(service.create(request, key));
    }
    @GetMapping("/{id}")
    public PaymentResponse get(@PathVariable UUID id) { return PaymentResponse.from(service.get(id)); }
    @GetMapping
    public List<PaymentResponse> list() { return service.list().stream().map(PaymentResponse::from).toList(); }
    @PatchMapping("/{id}/status")
    public PaymentResponse updateStatus(@PathVariable UUID id, @Valid @RequestBody UpdatePaymentStatusRequest request) {
        return PaymentResponse.from(service.updateStatus(id, request.status()));
    }
}
