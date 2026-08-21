CREATE TABLE IF NOT EXISTS audit_logs (
    id UUID PRIMARY KEY,
    entity_type VARCHAR(255) NOT NULL,
    entity_id UUID NOT NULL,
    action VARCHAR(255) NOT NULL,
    changed_by VARCHAR(255) NOT NULL,
    old_value TEXT,
    new_value TEXT,
    changed_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_audit_logs_entity_id ON audit_logs(entity_id);
CREATE INDEX idx_audit_logs_entity_type ON audit_logs(entity_type);
CREATE INDEX idx_audit_logs_changed_at ON audit_logs(changed_at);
