-- ============================================================
-- V14__issue_seed_coupons_to_guests.sql
-- 설명:
-- 1) guest1~guest5 (5명)
-- 2) bn1~bn5 (5개 매장) 각 매장(사장님) 템플릿 1개씩
-- 3) issued_coupon은 memo_id가 NOT NULL이므로
--    "시드 발급용 ADOPTED FREE 메모"를 먼저 만들고,
--    그 memo를 참조해 ISSUED 쿠폰을 발급한다.
-- ============================================================

SET FOREIGN_KEY_CHECKS = 0;

SET @bn1 := '1000000001';
SET @bn2 := '1000000002';
SET @bn3 := '1000000003';
SET @bn4 := '1000000004';
SET @bn5 := '1000000005';
SET @bn6 := '1000000006';

-- ============================================================
-- coupon_template image URL 일괄 수정 양식
-- (가게 business_number + title로 정확히 1개 템플릿만 타겟)
-- ============================================================

-- bn1: 카페 레이지아워
UPDATE coupon_template ct
    JOIN store s ON s.owner_id = ct.created_by_user_id
    SET ct.image = 'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/uploads/menu/AlexanderMenu1.jpeg'
WHERE s.business_number = '1000000001'
  AND ct.title = '아메리카노 1잔 무료 교환권';

-- bn2: 마이 디어 버터하우스
UPDATE coupon_template ct
    JOIN store s ON s.owner_id = ct.created_by_user_id
    SET ct.image = 'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/uploads/menu/LazyHourMenu2.jpeg'
WHERE s.business_number = '1000000002'
  AND ct.title = '디저트 20% 할인 쿠폰';

-- bn3: 도우터
UPDATE coupon_template ct
    JOIN store s ON s.owner_id = ct.created_by_user_id
    SET ct.image = 'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/uploads/menu/DaughterMenu1.png'
WHERE s.business_number = '1000000003'
  AND ct.title = '브런치 메뉴 10% 할인 쿠폰';

-- bn4: café 462
UPDATE coupon_template ct
    JOIN store s ON s.owner_id = ct.created_by_user_id
    SET ct.image = 'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/uploads/menu/Cafe462Menu_2.jpeg'
WHERE s.business_number = '1000000004'
  AND ct.title = '케이크 메뉴 무료 레터링';

-- bn5: 카페 언필드
UPDATE coupon_template ct
    JOIN store s ON s.owner_id = ct.created_by_user_id
    SET ct.image = 'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/uploads/menu/MyDearButterHouseMenu2.png'
WHERE s.business_number = '1000000005'
  AND ct.title = '빙수 메뉴 1천원 할인';

-- bn6: 더이퀄리브리엄커피
UPDATE coupon_template ct
    JOIN store s ON s.owner_id = ct.created_by_user_id
    SET ct.image = 'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/uploads/menu/CafeOnNAMenu3.jpeg'
WHERE s.business_number = '1000000006'
  AND ct.title = '밀크티 사이즈 업';


-- ------------------------------------------------------------
-- 0) 대상 유저/매장/템플릿 뷰(derived table)로 준비
--    - 템플릿은 "사장님(created_by_user_id=store.owner_id)" 기준 1개만 선택
--      (혹시 여러 개면 가장 작은 id 1개를 사용)
-- ------------------------------------------------------------

-- 1) 시드 발급용 메모 생성 (FREE, ADOPTED, concern_id 없음)
INSERT INTO memo (memo_type, free_type, title, content, status, store_id, user_id)
SELECT
    'FREE' AS memo_type,
    'TIP'  AS free_type,
    'SEED_COUPON' AS title,
    '시드 쿠폰 발급용 메모' AS content,
    'ADOPTED' AS status,
    s.id AS store_id,
    u.id AS user_id
FROM
    (SELECT id FROM users WHERE login_id IN ('guest1','guest2','guest3','guest4','guest5')) u
        CROSS JOIN
    (SELECT id, owner_id FROM store WHERE business_number IN (@bn1,@bn2,@bn3,@bn4,@bn5,@bn6)) s
        JOIN
    (
        SELECT ct1.*
        FROM coupon_template ct1
                 JOIN (
            SELECT created_by_user_id, MIN(id) AS min_id
            FROM coupon_template
            GROUP BY created_by_user_id
        ) pick ON pick.created_by_user_id = ct1.created_by_user_id
            AND pick.min_id = ct1.id
    ) ct
    ON ct.created_by_user_id = s.owner_id
WHERE NOT EXISTS (
    SELECT 1
    FROM memo m
    WHERE m.memo_type = 'FREE'
      AND m.title = 'SEED_COUPON'
      AND m.status = 'ADOPTED'
      AND m.store_id = s.id
      AND m.user_id = u.id
);

-- 2) issued_coupon 발급 (memo_id는 위에서 만든 SEED_COUPON 메모 참조)
INSERT INTO issued_coupon (
    store_id,
    memo_id,
    template_id,
    user_id,
    title,
    description,
    image,
    `condition`,
    issued_at,
    expired_at,
    status
)
SELECT
    s.id AS store_id,
    m.id AS memo_id,
    ct.id AS template_id,
    u.id AS user_id,
    ct.title,
    ct.description,
    ct.image,
    '매장 방문 후 제시' AS `condition`,
    NOW() AS issued_at,
    DATE_ADD(NOW(), INTERVAL ct.valid_days DAY) AS expired_at,
    'ISSUED' AS status
FROM
    (SELECT id FROM users WHERE login_id IN ('guest1','guest2','guest3','guest4','guest5')) u
        CROSS JOIN
    (SELECT id, owner_id FROM store WHERE business_number IN (@bn1,@bn2,@bn3,@bn4,@bn5,@bn6)) s
        JOIN
    (
        SELECT ct1.*
        FROM coupon_template ct1
                 JOIN (
            SELECT created_by_user_id, MIN(id) AS min_id
            FROM coupon_template
            GROUP BY created_by_user_id
        ) pick ON pick.created_by_user_id = ct1.created_by_user_id
            AND pick.min_id = ct1.id
    ) ct
    ON ct.created_by_user_id = s.owner_id
        JOIN memo m
             ON m.memo_type = 'FREE'
                 AND m.title = 'SEED_COUPON'
                 AND m.status = 'ADOPTED'
                 AND m.store_id = s.id
                 AND m.user_id = u.id
WHERE NOT EXISTS (
    SELECT 1
    FROM issued_coupon ic
    WHERE ic.store_id = s.id
      AND ic.user_id = u.id
      AND ic.template_id = ct.id
      AND ic.status IN ('ISSUED','USED')   -- 이미 발급/사용이 있으면 중복 방지
);

-- ============================================================
-- 3) decision 백필 (채택 완료 ANSWER에 대해 안정적으로 decision 생성)
--    - 서비스 로직에서 decision 존재를 전제로 하는 경우 대비
--    - FREE(시드 발급용) 메모는 제외하고, ANSWER + ADOPTED만 대상으로 함
--    - coupon_template_id는 concern.template_id로 채움
-- ============================================================

INSERT INTO decision (memo_id, type, coupon_template_id, created_at)
SELECT
    m.id,
    'ADOPT',
    CASE
        WHEN m.memo_type = 'ANSWER' THEN c.template_id
        ELSE NULL
        END AS coupon_template_id,
    NOW()
FROM memo m
LEFT JOIN concern c ON c.id = m.concern_id
WHERE m.status = 'ADOPTED'
  AND m.memo_type IN ('ANSWER', 'FREE')
  AND NOT EXISTS (
    SELECT 1
    FROM decision d
    WHERE d.memo_id = m.id
  );

SET FOREIGN_KEY_CHECKS = 1;
