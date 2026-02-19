-- ============================================================
-- R__02_insert_test_guest_template_concern_memo.sql
-- 설명:
-- 1) 더미 GUEST 5명
-- 2) 매장별 쿠폰 템플릿 (store.owner_id 기준)
-- 3) 사장님 고민거리 (concern) – 엑셀 기준 전체
-- 4) 사장님 고민 답변 / POSiT FREE 메모 – 엑셀 기준 전체
-- 5) decision (채택 완료건만)
-- ============================================================

SET FOREIGN_KEY_CHECKS = 0;

-- Resolve store ids by business_number (avoid relying on store.id or store.name)
SET @bn1 := '1000000001';
SET @bn2 := '1000000002';
SET @bn3 := '1000000003';
SET @bn4 := '1000000004';
SET @bn5 := '1000000005';
SET @bn6 := '1000000006';

SET @s1 := (SELECT id FROM store WHERE business_number = @bn1);
SET @s2 := (SELECT id FROM store WHERE business_number = @bn2);
SET @s3 := (SELECT id FROM store WHERE business_number = @bn3);
SET @s4 := (SELECT id FROM store WHERE business_number = @bn4);
SET @s5 := (SELECT id FROM store WHERE business_number = @bn5);
SET @s6 := (SELECT id FROM store WHERE business_number = @bn6);

-- ============================================================
-- 1. 더미 GUEST 5명
-- ============================================================
INSERT INTO users (
    role, login_id, password, name, phone, gender, birth, created_at, updated_at
) VALUES
      ('GUEST','guest1','$2a$10$T0kcl6QQcMc5oDMdOYvIRecSCH179zPaLY57Y/uEhQoOy93U8qyRO','손님1','01090000001','MALE','1998-01-01',NOW(),NOW()),
      ('GUEST','guest2','$2a$10$T0kcl6QQcMc5oDMdOYvIRecSCH179zPaLY57Y/uEhQoOy93U8qyRO','손님2','01090000002','FEMALE','1999-02-02',NOW(),NOW()),
      ('GUEST','guest3','$2a$10$T0kcl6QQcMc5oDMdOYvIRecSCH179zPaLY57Y/uEhQoOy93U8qyRO','손님3','01090000003','MALE','2000-03-03',NOW(),NOW()),
      ('GUEST','guest4','$2a$10$T0kcl6QQcMc5oDMdOYvIRecSCH179zPaLY57Y/uEhQoOy93U8qyRO','손님4','01090000004','FEMALE','2001-04-04',NOW(),NOW()),
      ('GUEST','guest5','$2a$10$T0kcl6QQcMc5oDMdOYvIRecSCH179zPaLY57Y/uEhQoOy93U8qyRO','손님5','01090000005','MALE','2002-05-05',NOW(),NOW());

-- ============================================================
-- 3. 쿠폰 템플릿 (OWNER 귀속)
-- ============================================================

-- 카페 레이지아워 (store.owner_id)
INSERT INTO coupon_template (title, description, image, valid_days, created_by_user_id)
SELECT
    '아메리카노 1잔 무료 교환권',
    '아메리카노 1잔 무료 제공',
    'https://example.com/coupon_americano.png',
    30,
    s.owner_id
FROM store s
WHERE s.business_number = @bn1
  AND s.owner_id IS NOT NULL
  AND NOT EXISTS (
    SELECT 1
    FROM coupon_template ct
    WHERE ct.created_by_user_id = s.owner_id
      AND ct.title = '아메리카노 1잔 무료 교환권'
  );

-- 마이 디어 버터하우스 (store.owner_id)
INSERT INTO coupon_template (title, description, image, valid_days, created_by_user_id)
SELECT
    '디저트 20% 할인 쿠폰',
    '디저트 메뉴 20% 할인',
    'https://example.com/coupon_dessert.png',
    14,
    s.owner_id
FROM store s
WHERE s.business_number = @bn2
  AND s.owner_id IS NOT NULL
  AND NOT EXISTS (
    SELECT 1
    FROM coupon_template ct
    WHERE ct.created_by_user_id = s.owner_id
      AND ct.title = '디저트 20% 할인 쿠폰'
  );

-- 도우터
INSERT INTO coupon_template (title, description, image, valid_days, created_by_user_id)
SELECT '브런치 메뉴 10% 할인 쿠폰', '브런치 메뉴 10% 할인', 'https://example.com/coupon_brunch.png', 14, s.owner_id
FROM store s
WHERE s.business_number = @bn3
  AND s.owner_id IS NOT NULL
  AND NOT EXISTS (
    SELECT 1
    FROM coupon_template ct
    WHERE ct.created_by_user_id = s.owner_id
      AND ct.title = '브런치 메뉴 10% 할인 쿠폰'
  );

-- café 462
INSERT INTO coupon_template (title, description, image, valid_days, created_by_user_id)
SELECT '케이크 메뉴 무료 레터링', '케이크 구매 시 무료 레터링', 'https://example.com/coupon_cake.png', 30, s.owner_id
FROM store s
WHERE s.business_number = @bn4
  AND s.owner_id IS NOT NULL
  AND NOT EXISTS (
    SELECT 1
    FROM coupon_template ct
    WHERE ct.created_by_user_id = s.owner_id
      AND ct.title = '케이크 메뉴 무료 레터링'
  );

-- 카페 언필드
INSERT INTO coupon_template (title, description, image, valid_days, created_by_user_id)
SELECT '빙수 메뉴 1천원 할인', '빙수 메뉴 1천원 할인', 'https://example.com/coupon_bingsu.png', 21, s.owner_id
FROM store s
WHERE s.business_number = @bn5
  AND s.owner_id IS NOT NULL
  AND NOT EXISTS (
    SELECT 1
    FROM coupon_template ct
    WHERE ct.created_by_user_id = s.owner_id
      AND ct.title = '빙수 메뉴 1천원 할인'
  );

-- 더이퀄리브리엄커피
INSERT INTO coupon_template (title, description, image, valid_days, created_by_user_id)
SELECT '밀크티 사이즈 업', '밀크티 무료 사이즈 업', 'https://example.com/coupon_milktea.png', 21, s.owner_id
FROM store s
WHERE s.business_number = @bn6
  AND s.owner_id IS NOT NULL
  AND NOT EXISTS (
    SELECT 1
    FROM coupon_template ct
    WHERE ct.created_by_user_id = s.owner_id
      AND ct.title = '밀크티 사이즈 업'
  );

-- ============================================================
-- 4. concern (사장님 고민거리)
-- ============================================================

INSERT INTO concern (content, status, store_id, template_id)
SELECT
    '매장 조명을 조금 더 밝게 바꿔야 할까요?',
    'OPEN',
    s.id,
    ct.id
FROM store s
JOIN coupon_template ct ON ct.created_by_user_id = s.owner_id
WHERE s.business_number = @bn1
  AND NOT EXISTS (
    SELECT 1
    FROM concern c
    WHERE c.store_id = s.id
      AND c.content = '매장 조명을 조금 더 밝게 바꿔야 할까요?'
  );

-- sample1 추가 고민
INSERT INTO concern (content, status, store_id, template_id)
SELECT '새로운 두바이 디저트 메뉴로 어떤 것이 좋을까요?', 'OPEN', s.id, ct.id
FROM store s JOIN coupon_template ct ON ct.created_by_user_id=s.owner_id
WHERE s.business_number = @bn1
  AND NOT EXISTS (
    SELECT 1
    FROM concern c
    WHERE c.store_id = s.id
      AND c.content = '새로운 두바이 디저트 메뉴로 어떤 것이 좋을까요?'
  );

-- sample2
INSERT INTO concern (content, status, store_id, template_id)
SELECT '키티 휘낭시에 맛을 다양화할까요?', 'OPEN', s.id, ct.id
FROM store s JOIN coupon_template ct ON ct.created_by_user_id=s.owner_id
WHERE s.business_number = @bn2
  AND NOT EXISTS (
    SELECT 1
    FROM concern c
    WHERE c.store_id = s.id
      AND c.content = '키티 휘낭시에 맛을 다양화할까요?'
  );

INSERT INTO concern (content, status, store_id, template_id)
SELECT '티라미수에 코코아 파우더가 너무 많나요?', 'OPEN', s.id, ct.id
FROM store s JOIN coupon_template ct ON ct.created_by_user_id=s.owner_id
WHERE s.business_number = @bn2
  AND NOT EXISTS (
    SELECT 1
    FROM concern c
    WHERE c.store_id = s.id
      AND c.content = '티라미수에 코코아 파우더가 너무 많나요?'
  );

-- sample3
INSERT INTO concern (content, status, store_id, template_id)
SELECT '플레이트에 바질 추가는 어떤가요?', 'OPEN', s.id, ct.id
FROM store s JOIN coupon_template ct ON ct.created_by_user_id=s.owner_id
WHERE s.business_number = @bn3
  AND NOT EXISTS (
    SELECT 1
    FROM concern c
    WHERE c.store_id = s.id
      AND c.content = '플레이트에 바질 추가는 어떤가요?'
  );

-- sample4
INSERT INTO concern (content, status, store_id, template_id)
SELECT '단일 초코 케이크를 메뉴에 추가할까요?', 'OPEN', s.id, ct.id
FROM store s JOIN coupon_template ct ON ct.created_by_user_id=s.owner_id
WHERE s.business_number = @bn4
  AND NOT EXISTS (
    SELECT 1
    FROM concern c
    WHERE c.store_id = s.id
      AND c.content = '단일 초코 케이크를 메뉴에 추가할까요?'
  );

-- sample5
INSERT INTO concern (content, status, store_id, template_id)
SELECT '프렌치토스트에 과일 추가 시 가격이 얼마가 적당할까요?', 'OPEN', s.id, ct.id
FROM store s JOIN coupon_template ct ON ct.created_by_user_id=s.owner_id
WHERE s.business_number = @bn5
  AND NOT EXISTS (
    SELECT 1
    FROM concern c
    WHERE c.store_id = s.id
      AND c.content = '프렌치토스트에 과일 추가 시 가격이 얼마가 적당할까요?'
  );

-- sample6
INSERT INTO concern (content, status, store_id, template_id)
SELECT '디저트 류에 제철과일을 같이 곁들이는 건 어떤가요?', 'OPEN', s.id, ct.id
FROM store s JOIN coupon_template ct ON ct.created_by_user_id=s.owner_id
WHERE s.business_number = @bn6
  AND NOT EXISTS (
    SELECT 1
    FROM concern c
    WHERE c.store_id = s.id
      AND c.content = '디저트 류에 제철과일을 같이 곁들이는 건 어떤가요?'
  );

-- ============================================================
-- 5. memo (ANSWER / FREE)
-- ============================================================

INSERT INTO memo (memo_type, title, content, status, store_id, user_id, concern_id)
SELECT
    'ANSWER',
    '노랑 조명 대신 따뜻한 화이트 톤 조명 어떨까요?',
    '노랑 조명 대신 따뜻한 화이트 톤 조명 어떨까요?',
    'ADOPTED',
    s.id,
    u.id,
    c.id
FROM store s
JOIN users u ON u.login_id='guest1'
JOIN concern c ON c.store_id=s.id
             AND c.content='매장 조명을 조금 더 밝게 바꿔야 할까요?'
WHERE s.business_number = @bn1
  AND NOT EXISTS (
    SELECT 1
    FROM memo m
    WHERE m.memo_type='ANSWER'
      AND m.store_id=s.id
      AND m.user_id=u.id
      AND m.concern_id=c.id
      AND m.title='노랑 조명 대신 따뜻한 화이트 톤 조명 어떨까요?'
  );

INSERT INTO memo (memo_type, free_type, title, content, status, store_id, user_id)
SELECT
    'FREE',
    'MENU_DEV',
    '브뤨레 에그타르트 메뉴 추가 원해요~!',
    '브뤨레 에그타르트 메뉴 추가 원해요~!',
    'ADOPTED',
    s.id,
    u.id
FROM store s
JOIN users u ON u.login_id='guest2'
WHERE s.business_number = @bn1
  AND NOT EXISTS (
    SELECT 1
    FROM memo m
    WHERE m.memo_type='FREE'
      AND m.store_id=s.id
      AND m.user_id=u.id
      AND m.title='브뤨레 에그타르트 메뉴 추가 원해요~!'
  );

-- sample2 ANSWER
INSERT INTO memo (memo_type, title, content, status, store_id, user_id, concern_id)
SELECT 'ANSWER','말차 맛이 추가되면 좋을거같아요!','말차 맛이 추가되면 좋을거같아요!','REVIEWING',s.id,u.id,c.id
FROM store s JOIN users u ON u.login_id='guest3' JOIN concern c ON c.store_id=s.id AND c.content='키티 휘낭시에 맛을 다양화할까요?'
WHERE s.business_number = @bn2
  AND NOT EXISTS (
    SELECT 1
    FROM memo m
    WHERE m.memo_type='ANSWER'
      AND m.store_id=s.id
      AND m.user_id=u.id
      AND m.concern_id=c.id
      AND m.title='말차 맛이 추가되면 좋을거같아요!'
  );

-- sample3 ANSWER
INSERT INTO memo (memo_type, title, content, status, store_id, user_id, concern_id)
SELECT 'ANSWER','바질 향이 브런치랑 잘 어울려요.','바질 향이 브런치랑 잘 어울려요.','ADOPTED',s.id,u.id,c.id
FROM store s JOIN users u ON u.login_id='guest1' JOIN concern c ON c.store_id=s.id AND c.content='플레이트에 바질 추가는 어떤가요?'
WHERE s.business_number = @bn3
  AND NOT EXISTS (
    SELECT 1
    FROM memo m
    WHERE m.memo_type='ANSWER'
      AND m.store_id=s.id
      AND m.user_id=u.id
      AND m.concern_id=c.id
      AND m.title='바질 향이 브런치랑 잘 어울려요.'
  );

-- sample4 ANSWER
INSERT INTO memo (memo_type, title, content, status, store_id, user_id, concern_id)
SELECT 'ANSWER','꾸덕한 초코 케이크 좋아요!','꾸덕한 초코 케이크 좋아요!','ADOPTED',s.id,u.id,c.id
FROM store s JOIN users u ON u.login_id='guest2' JOIN concern c ON c.store_id=s.id AND c.content='단일 초코 케이크를 메뉴에 추가할까요?'
WHERE s.business_number = @bn4
  AND NOT EXISTS (
    SELECT 1
    FROM memo m
    WHERE m.memo_type='ANSWER'
      AND m.store_id=s.id
      AND m.user_id=u.id
      AND m.concern_id=c.id
      AND m.title='꾸덕한 초코 케이크 좋아요!'
  );

-- sample5 ANSWER
INSERT INTO memo (memo_type, title, content, status, store_id, user_id, concern_id)
SELECT 'ANSWER','제철 과일을 올리면 좋을거같은데, 가격이 오를까봐 걱정이네요.','제철 과일을 올리면 좋을거같은데, 가격이 오를까봐 걱정이네요.','REVIEWING',s.id,u.id,c.id
FROM store s JOIN users u ON u.login_id='guest4' JOIN concern c ON c.store_id=s.id AND c.content='프렌치토스트에 과일 추가 시 가격이 얼마가 적당할까요?'
WHERE s.business_number = @bn5
  AND NOT EXISTS (
    SELECT 1
    FROM memo m
    WHERE m.memo_type='ANSWER'
      AND m.store_id=s.id
      AND m.user_id=u.id
      AND m.concern_id=c.id
      AND m.title='제철 과일을 올리면 좋을거같은데, 가격이 오를까봐 걱정이네요.'
  );

-- sample6 FREE
INSERT INTO memo (memo_type, free_type, title, content, status, store_id, user_id)
SELECT 'FREE','MENU_DEV','양이 적은 밀크티 출시 원해요.','양이 적은 밀크티 출시 원해요.','REVIEWING',s.id,u.id
FROM store s JOIN users u ON u.login_id='guest5'
WHERE s.business_number = @bn6
  AND NOT EXISTS (
    SELECT 1
    FROM memo m
    WHERE m.memo_type='FREE'
      AND m.store_id=s.id
      AND m.user_id=u.id
      AND m.title='양이 적은 밀크티 출시 원해요.'
  );

-- ============================================================
-- 6. decision (채택 완료건)
-- ============================================================

INSERT INTO decision (memo_id, type)
SELECT m.id, 'ADOPT'
FROM memo m
WHERE m.status='ADOPTED'
  AND m.memo_type='ANSWER'
  AND NOT EXISTS (
    SELECT 1
    FROM decision d
    WHERE d.memo_id = m.id
  );

SET FOREIGN_KEY_CHECKS = 1;