ALTER TABLE memo
    ADD COLUMN owner_read TINYINT(1) NOT NULL DEFAULT 0;
-- 기존 메모 데이터 처리
UPDATE memo SET owner_read = 0 WHERE owner_read IS NULL;

UPDATE store
SET coupon_pin_hash = '$2a$10$xl9mSw0arY0MbyaC9jQsn.Z2WzL/eqoYXj0RHL2CURIo/2B1VC.1e'
WHERE coupon_pin_hash IS NULL;