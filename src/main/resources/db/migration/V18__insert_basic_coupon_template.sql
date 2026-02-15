-- ============================================================
-- V18__insert_basic_coupon_template.sql
-- 목적:
--  - DB를 내리지 않고, 모든 사장님(OWNER)에게 기본 쿠폰 템플릿 3종
--    (아메리카노/디저트/아이스티)을 "동일한 내용/이미지"로 보장한다.
-- ============================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ------------------------------------------------------------
-- 1) 기본 3종 템플릿이 없으면 INSERT (owner별)
--    - title + created_by_user_id 조합으로 존재 여부 체크
-- ------------------------------------------------------------

-- 1-1) 아메리카노
INSERT INTO coupon_template (title, description, image, valid_days, created_by_user_id)
SELECT
    '아메리카노 1잔 무료 교환권' AS title,
    '아메리카노 1잔 무료 제공'  AS description,
    'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/uploads/menu/AlexanderMenu1.jpeg' AS image,
    30 AS valid_days,
    s.owner_id AS created_by_user_id
FROM store s
JOIN users u ON u.id = s.owner_id
WHERE s.owner_id IS NOT NULL
  AND u.role = 'OWNER'
  AND NOT EXISTS (
      SELECT 1
      FROM coupon_template ct
      WHERE ct.created_by_user_id = s.owner_id
        AND ct.title = '아메리카노 1잔 무료 교환권'
  );

-- 1-2) 디저트
INSERT INTO coupon_template (title, description, image, valid_days, created_by_user_id)
SELECT
    '디저트 20% 할인 쿠폰' AS title,
    '디저트 메뉴 20% 할인' AS description,
    'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/uploads/menu/LazyHourMenu3.jpeg' AS image,
    30 AS valid_days,
    s.owner_id AS created_by_user_id
FROM store s
JOIN users u ON u.id = s.owner_id
WHERE s.owner_id IS NOT NULL
  AND u.role = 'OWNER'
  AND NOT EXISTS (
      SELECT 1
      FROM coupon_template ct
      WHERE ct.created_by_user_id = s.owner_id
        AND ct.title = '디저트 20% 할인 쿠폰'
  );

-- 1-3) 아이스티
INSERT INTO coupon_template (title, description, image, valid_days, created_by_user_id)
SELECT
    '아이스티 1잔 무료 교환권' AS title,
    '아이스티 1잔 무료 제공'  AS description,
    'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/uploads/menu/ASMenu3.jpeg' AS image,
    30 AS valid_days,
    s.owner_id AS created_by_user_id
FROM store s
JOIN users u ON u.id = s.owner_id
WHERE s.owner_id IS NOT NULL
  AND u.role = 'OWNER'
  AND NOT EXISTS (
      SELECT 1
      FROM coupon_template ct
      WHERE ct.created_by_user_id = s.owner_id
        AND ct.title = '아이스티 1잔 무료 교환권'
  );


-- ------------------------------------------------------------
-- 2) 기본 3종 템플릿 내용을 "표준값"으로 UPDATE
--    (이미 존재하던 행의 image/description/valid_days도 통일)
-- ------------------------------------------------------------

UPDATE coupon_template
SET
    description = '아메리카노 1잔 무료 제공',
    image = 'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/uploads/menu/AlexanderMenu1.jpeg',
    valid_days = 30,
    updated_at = NOW()
WHERE title = '아메리카노 1잔 무료 교환권'
  AND created_by_user_id IN (
      SELECT DISTINCT s.owner_id
      FROM store s
      JOIN users u ON u.id = s.owner_id
      WHERE s.owner_id IS NOT NULL
        AND u.role = 'OWNER'
  );

UPDATE coupon_template
SET
    description = '디저트 메뉴 20% 할인',
    image = 'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/uploads/menu/LazyHourMenu3.jpeg',
    valid_days = 30,
    updated_at = NOW()
WHERE title = '디저트 20% 할인 쿠폰'
  AND created_by_user_id IN (
      SELECT DISTINCT s.owner_id
      FROM store s
      JOIN users u ON u.id = s.owner_id
      WHERE s.owner_id IS NOT NULL
        AND u.role = 'OWNER'
  );


UPDATE coupon_template
SET
    description = '아이스티 1잔 무료 제공',
    image = 'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/uploads/menu/ASMenu3.jpeg',
    valid_days = 30,
    updated_at = NOW()
WHERE title = '아이스티 1잔 무료 교환권'
  AND created_by_user_id IN (
      SELECT DISTINCT s.owner_id
      FROM store s
      JOIN users u ON u.id = s.owner_id
      WHERE s.owner_id IS NOT NULL
        AND u.role = 'OWNER'
  );


-- ------------------------------------------------------------
-- 2.5) (정리) 레거시 템플릿을 참조하는 FK들을 표준 템플릿으로 재매핑
--      - 과거 V10/V14에서 owner별로 1개씩 만들었던 레거시 템플릿(id=1~6 등)이
--        concern / decision / issued_coupon에서 참조 중이면 삭제가 불가하다.
-- ------------------------------------------------------------

-- (A) concern.template_id -> 표준 아메리카노 템플릿으로 이동
UPDATE concern c
JOIN store s ON s.id = c.store_id
JOIN (
    SELECT
        created_by_user_id AS owner_id,
        MAX(CASE WHEN image = 'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/uploads/menu/AlexanderMenu1.jpeg' THEN id END) AS americano_id,
        MAX(CASE WHEN image = 'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/uploads/menu/LazyHourMenu3.jpeg' THEN id END) AS dessert_id,
        MAX(CASE WHEN image = 'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/uploads/menu/ASMenu3.jpeg' THEN id END) AS icetea_id
    FROM coupon_template
    GROUP BY created_by_user_id
) std ON std.owner_id = s.owner_id
SET c.template_id = std.americano_id
WHERE c.template_id IS NOT NULL
  AND c.template_id NOT IN (std.americano_id, std.dessert_id, std.icetea_id);


-- (B) decision.coupon_template_id -> 표준 아메리카노 템플릿으로 이동
--     * ANSWER 채택건은 보통 concern.template_id를 따라가므로 (A) 이후엔 자동 정리되지만,
--       과거 데이터/백필로 인해 decision이 직접 레거시를 물고 있을 수 있어 별도 보정.
UPDATE decision d
JOIN memo m ON m.id = d.memo_id
JOIN store s ON s.id = m.store_id
JOIN (
    SELECT
        created_by_user_id AS owner_id,
        MAX(CASE WHEN image = 'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/uploads/menu/AlexanderMenu1.jpeg' THEN id END) AS americano_id,
        MAX(CASE WHEN image = 'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/uploads/menu/LazyHourMenu3.jpeg' THEN id END) AS dessert_id,
        MAX(CASE WHEN image = 'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/uploads/menu/ASMenu3.jpeg' THEN id END) AS icetea_id
    FROM coupon_template
    GROUP BY created_by_user_id
) std ON std.owner_id = s.owner_id
SET d.coupon_template_id = std.americano_id
WHERE d.coupon_template_id IS NOT NULL
  AND d.coupon_template_id NOT IN (std.americano_id, std.dessert_id, std.icetea_id);


-- (C) issued_coupon.template_id -> 표준 아메리카노 템플릿으로 이동
--     * issued_coupon은 title/description/image를 자체 저장하고 있어(template_id 변경 영향이 작음),
--       레거시 템플릿 삭제를 위해 참조만 표준 템플릿으로 이동.
UPDATE issued_coupon ic
JOIN store s ON s.id = ic.store_id
JOIN (
    SELECT
        created_by_user_id AS owner_id,
        MAX(CASE WHEN image = 'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/uploads/menu/AlexanderMenu1.jpeg' THEN id END) AS americano_id,
        MAX(CASE WHEN image = 'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/uploads/menu/LazyHourMenu3.jpeg' THEN id END) AS dessert_id,
        MAX(CASE WHEN image = 'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/uploads/menu/ASMenu3.jpeg' THEN id END) AS icetea_id
    FROM coupon_template
    GROUP BY created_by_user_id
) std ON std.owner_id = s.owner_id
SET ic.template_id = std.americano_id
WHERE ic.template_id IS NOT NULL
  AND ic.template_id NOT IN (std.americano_id, std.dessert_id, std.icetea_id);


-- ------------------------------------------------------------
-- 3) (선택) 표준 3종이 아닌 "기타 템플릿" 중 FK로 참조되지 않는 것만 삭제
--      어디에도 걸려있지 않은 템플릿만 제거 가능
-- ------------------------------------------------------------

DELETE ct
FROM coupon_template ct
JOIN users u ON u.id = ct.created_by_user_id
WHERE u.role = 'OWNER'
  AND (ct.image IS NULL OR ct.image NOT IN (
      'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/uploads/menu/AlexanderMenu1.jpeg',
      'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/uploads/menu/LazyHourMenu3.jpeg',
      'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/uploads/menu/ASMenu3.jpeg'
  ))
  AND NOT EXISTS (
      SELECT 1 FROM issued_coupon ic WHERE ic.template_id = ct.id
  )
  AND NOT EXISTS (
      SELECT 1 FROM concern c WHERE c.template_id = ct.id
  )
  AND NOT EXISTS (
      SELECT 1 FROM decision d WHERE d.coupon_template_id = ct.id
  );


SET FOREIGN_KEY_CHECKS = 1;