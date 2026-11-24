package com.mohith.payflow.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "payments", uniqueConstraints = @UniqueConstraint(name = "uk_payment_idempotency", columnNames = "idempotency_key"))
public class Payment {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(name = "customer_reference", nullable = false, length = 80)
    private String customerReference;
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;
    @Column(nullable = false, length = 3)
    private String currency;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20)
    private PaymentStatus status;
    @Column(name = "idempotency_key", nullable = false, length = 100)
    private String idempotencyKey;
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected Payment() {}
    public Payment(String customerReference, BigDecimal amount, String currency, String idempotencyKey) {
        this.customerReference = customerReference;
        this.amount = amount;
        this.currency = currency;
        this.idempotencyKey = idempotencyKey;
        this.status = PaymentStatus.PENDING;
        this.createdAt = Instant.now();
    }
    public UUID getId() { return id; }
    public String getCustomerReference() { return customerReference; }
    public BigDecimal getAmount() { return amount; }
    public String getCurrency() { return currency; }
    public PaymentStatus getStatus() { return status; }
    public String getIdempotencyKey() { return idempotencyKey; }
    public Instant getCreatedAt() { return createdAt; }
    public void setStatus(PaymentStatus status) { this.status = status; }
}
