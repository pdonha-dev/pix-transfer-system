CREATE TABLE IF NOT EXISTS event_store (
    id UUID PRIMARY KEY,
    event_id UUID NOT NULL UNIQUE,
    event_type VARCHAR(255) NOT NULL,
    aggregate_id UUID NOT NULL,
    aggregate_type VARCHAR(255) NOT NULL,
    aggregate_version INT NOT NULL,
    event_data TEXT NOT NULL,
    stored_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_event_store_aggregate_id ON event_store(aggregate_id);
CREATE INDEX idx_event_store_aggregate_type ON event_store(aggregate_type);
CREATE INDEX idx_event_store_aggregate_version ON event_store(aggregate_id, aggregate_version);
