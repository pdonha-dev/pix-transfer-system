UPDATE idempotency_keys
SET request_hash = 'legacy:' || id::text
WHERE request_hash = md5(key) || md5('legacy:' || key);
