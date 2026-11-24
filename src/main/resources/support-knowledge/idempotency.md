# Idempotency and duplicate requests

Every payment creation request must include an Idempotency-Key header. Reusing the same key returns the original payment record instead of creating another record. A client should use a unique key for each new intended payment and reuse the same key only when retrying that same request after a timeout or uncertain network response.
