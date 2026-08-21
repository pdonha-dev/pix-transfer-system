ALTER TABLE accounts ADD COLUMN created_by VARCHAR(255);
ALTER TABLE accounts ADD COLUMN last_modified_by VARCHAR(255);
ALTER TABLE accounts ADD COLUMN deleted_at TIMESTAMP;

ALTER TABLE transfers ADD COLUMN created_by VARCHAR(255);
ALTER TABLE transfers ADD COLUMN last_modified_by VARCHAR(255);
ALTER TABLE transfers ADD COLUMN deleted_at TIMESTAMP;
