CREATE TABLE IF NOT EXISTS idempotency_keys (
    id UUID PRIMARY KEY,
    key VARCHAR(255) NOT NULL UNIQUE,
    transfer_id UUID NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_idempotency_keys_key ON idempotency_keys(key);
CREATE INDEX idx_idempotency_keys_transfer_id ON idempotency_keys(transfer_id);
CREATE INDEX idx_idempotency_keys_status ON idempotency_keys(status);
