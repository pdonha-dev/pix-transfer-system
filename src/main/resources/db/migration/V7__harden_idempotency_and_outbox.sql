ALTER TABLE idempotency_keys
    ADD COLUMN request_hash VARCHAR(64);

UPDATE idempotency_keys
SET request_hash = md5(key) || md5('legacy:' || key)
WHERE request_hash IS NULL;

ALTER TABLE idempotency_keys
    ALTER COLUMN request_hash SET NOT NULL;

ALTER TABLE event_store
    ADD COLUMN publication_status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    ADD COLUMN publication_attempts INTEGER NOT NULL DEFAULT 0,
    ADD COLUMN published_at TIMESTAMP,
    ADD COLUMN next_attempt_at TIMESTAMP;

ALTER TABLE event_store
    ADD CONSTRAINT uk_event_store_aggregate_version
        UNIQUE (aggregate_id, aggregate_version);

CREATE INDEX idx_event_store_publication
    ON event_store(publication_status, next_attempt_at);
