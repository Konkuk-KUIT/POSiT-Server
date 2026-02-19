-- ============================================================
-- 1. store.owner_id UNIQUE 제약조건 (없을 때만 생성)
-- ============================================================

SET @exists := (
  SELECT COUNT(*)
  FROM information_schema.TABLE_CONSTRAINTS
  WHERE CONSTRAINT_SCHEMA = DATABASE()
    AND TABLE_NAME = 'store'
    AND CONSTRAINT_NAME = 'uq_store_owner'
    AND CONSTRAINT_TYPE = 'UNIQUE'
);

SET @sql := IF(@exists = 0,
  'ALTER TABLE store ADD CONSTRAINT uq_store_owner UNIQUE (owner_id)',
  'SELECT "uq_store_owner already exists"'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- ============================================================
-- 4. decision → coupon_template 매핑 (없는 것만)
-- ============================================================

UPDATE decision d
    JOIN memo m   ON m.id = d.memo_id
    JOIN store s  ON s.id = m.store_id
    JOIN users u  ON u.id = s.owner_id
    JOIN coupon_template ct
    ON ct.created_by_user_id = u.id
    AND ct.title = '아메리카노 1잔 무료 교환권'
    SET d.coupon_template_id = ct.id
WHERE d.type = 'ADOPT'
  AND d.coupon_template_id IS NULL;


-- ============================================================
-- 5. issued_coupon 생성 (중복 방지)
-- ============================================================

INSERT INTO issued_coupon (
    store_id, memo_id, template_id, user_id,
    title, description, image, `condition`,
    issued_at, expired_at, status
)
SELECT
    m.store_id,
    m.id,
    d.coupon_template_id,
    m.user_id,
    ct.title,
    ct.description,
    ct.image,
    '채택 보상 쿠폰',
    NOW(),
    DATE_ADD(NOW(), INTERVAL ct.valid_days DAY),
    'ISSUED'
FROM memo m
         JOIN decision d ON d.memo_id = m.id AND d.type='ADOPT'
         JOIN coupon_template ct ON ct.id = d.coupon_template_id
         LEFT JOIN issued_coupon ic ON ic.memo_id = m.id
WHERE m.status='ADOPTED'
  AND ic.id IS NULL;


-- ============================================================
-- 6. issued_coupon 동기화
-- ============================================================

UPDATE issued_coupon ic
    JOIN decision d ON d.memo_id = ic.memo_id AND d.type='ADOPT'
    JOIN coupon_template ct ON ct.id = d.coupon_template_id
    SET
        ic.template_id = d.coupon_template_id,
        ic.title = ct.title,
        ic.description = ct.description,
        ic.image = ct.image,
        ic.expired_at = DATE_ADD(ic.issued_at, INTERVAL ct.valid_days DAY)
WHERE ic.template_id <> d.coupon_template_id;


-- ============================================================
-- 7. signup_token_hash 컬럼 (없을 때만 생성)
-- ============================================================

SET @col_exists := (
  SELECT COUNT(*)
  FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'phone_verification'
    AND COLUMN_NAME = 'signup_token_hash'
);

SET @sql := IF(@col_exists = 0,
  'ALTER TABLE phone_verification ADD COLUMN signup_token_hash VARCHAR(200) NULL AFTER verified_at',
  'SELECT "signup_token_hash already exists"'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;