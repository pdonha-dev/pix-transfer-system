-- Accounts table
CREATE TABLE IF NOT EXISTS accounts (
    id UUID PRIMARY KEY,
    customer_id UUID NOT NULL,
    balance NUMERIC(19, 2) NOT NULL DEFAULT 0.00,
    daily_limit NUMERIC(19, 2) NOT NULL DEFAULT 0.00,
    daily_used NUMERIC(19, 2) NOT NULL DEFAULT 0.00,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_accounts_customer_id ON accounts(customer_id);

-- PIX Keys table
CREATE TABLE IF NOT EXISTS pix_keys (
    id UUID PRIMARY KEY,
    account_id UUID NOT NULL REFERENCES accounts(id) ON DELETE CASCADE,
    key_type VARCHAR(50) NOT NULL,
    key_value VARCHAR(255) NOT NULL UNIQUE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_pix_keys_account_id ON pix_keys(account_id);
CREATE INDEX idx_pix_keys_key_value ON pix_keys(key_value);

-- Transfers table
CREATE TABLE IF NOT EXISTS transfers (
    id UUID PRIMARY KEY,
    payer_account_id UUID NOT NULL REFERENCES accounts(id) ON DELETE RESTRICT,
    payee_account_id UUID NOT NULL REFERENCES accounts(id) ON DELETE RESTRICT,
    amount NUMERIC(19, 2) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_transfers_payer_account_id ON transfers(payer_account_id);
CREATE INDEX idx_transfers_payee_account_id ON transfers(payee_account_id);
CREATE INDEX idx_transfers_status ON transfers(status);
CREATE INDEX idx_transfers_created_at ON transfers(created_at);
