-- cafe 462 (store_id=4) 메뉴 이미지 누락 복구
UPDATE menu
SET image = 'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/uploads/menu/Cafe462Menu_1.jpeg'
WHERE store_id = 4 AND sort_order = 1 AND (image IS NULL OR image = '');

UPDATE menu
SET image = 'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/uploads/menu/Cafe462Menu_2.jpeg'
WHERE store_id = 4 AND sort_order = 2 AND (image IS NULL OR image = '');

-- ===========================
-- V12 : 선릉역 인근 카페 6개 추가
-- ===========================
-- 7. 카페온나 선릉점 (business_number=1000000007)
INSERT INTO store (name, road_address, open_time, not_open, phone, description, category, latitude, longitude, sns_link, business_number)
VALUES (
           '카페온나 선릉점',
           '서울 강남구 테헤란로47길 8 1층 106호',
           '09:30-21:00',
           'SUN',
           '0507-2093-7913',
           '아늑한 공간에서 즐기는 여유로운 한잔',
           'CAFE', 37.504215,127.045434,
           'https://www.instagram.com/cafe.onna_seolleung/profilecard/?igsh=MTVvOTFvZHBpZDI4bg%3D%3D',
           '1000000007'
       );

-- 8. 알렉산더 커피 스튜디오 (business_number=1000000008)
INSERT INTO store (name, road_address, open_time, not_open, phone, description, category, latitude, longitude, sns_link, business_number)
VALUES (
           '알렉산더 커피 스튜디오',
           '서울 강남구 테헤란로51길 23 1층 알렉산더 커피 스튜디오',
           '07:00-18:00',
           NULL,
           '0507-1303-4128',
           '아침 커피로 시작되는 기분 좋은 하루',
           'CAFE',
           37.505190, 127.045745,
           NULL,
           '1000000008'
       );

-- 9. 에이에스 커피 (business_number=1000000009)
INSERT INTO store (name, road_address, open_time, not_open, phone, description, category, latitude, longitude, sns_link, business_number)
VALUES (
           '에이에스 커피',
           '서울 강남구 선릉로 424 1동 1층 1호',
           '07:30-22:00',
           NULL,
           '02-2052-2023',
           '친절한 직원과 함께하는 맛있는 시간',
           'CAFE',
           37.502908, 127.050058,
           NULL,
           '1000000009'
       );

-- 10. 언노운 커피 (business_number=1000000010)
INSERT INTO store (name, road_address, open_time, not_open, phone, description, category, latitude, longitude, sns_link, business_number)
VALUES (
           '언노운 커피',
           '서울 강남구 역삼로 409 지상1층 106호 언노운커피',
           '07:00-20:00',
           NULL,
           '0507-1338-4146',
           '새로 오픈한 감성 카페의 맛있는 커피',
           'CAFE',
           37.501076, 127.051788,
           'https://www.instagram.com/unk.nowncoffee',
           '1000000010'
       );

-- 11. 텟어텟 선릉 (business_number=1000000011)
INSERT INTO store (name, road_address, open_time, not_open, phone, description, category, latitude, longitude, sns_link, business_number)
VALUES (
           '텟어텟 선릉',
           '서울 강남구 선릉로86길 10-5 . 지상1층',
           '09:00-23:00',
           NULL,
           '02-6203-1500',
           '상큼함이 톡톡 터지는 에이드 한 잔',
           'CAFE',
           37.502920, 127.050997,
           'https://www.instagram.com/tete.a.tete.coffee?igsh=M3Y4anR2bTZvb2pj',
           '1000000011'
       );

-- 12. 카페 레이어프로젝트 (business_number=1000000012)
INSERT INTO store (name, road_address, open_time, not_open, phone, description, category, latitude, longitude, sns_link, business_number)
VALUES (
           '카페 레이어프로젝트',
           '서울 강남구 역삼로63길 19 1,2,3층',
           '07:00-01:00',
           NULL,
           '02-501-3920',
           '밀크티와 베이커리의 완벽한 조화',
           'CAFE',
           37.502374, 127.051939,
           'http://instagram.com/cafelayerstudio',
           '1000000012'
       );

-- -----------------------------------------------------------------------------
-- 더미 사장(owner7~owner12) 생성 + owner_profile + store.owner_id 연결
-- -----------------------------------------------------------------------------

-- 공통 비밀번호 해시(기존과 동일)
SET @PW := '$2a$10$T0kcl6QQcMc5oDMdOYvIRecSCH179zPaLY57Y/uEhQoOy93U8qyRO';

-- owner7
INSERT INTO users (role, login_id, password, name, phone, gender, birth, created_at, updated_at)
VALUES ('OWNER', 'owner7', @PW, '온나사장', '01077777777', 'FEMALE', '1993-07-07', NOW(), NOW());
INSERT INTO owner_profile (user_id, business_number)
SELECT id, '1000000007' FROM users WHERE login_id='owner7';
UPDATE store SET owner_id = (SELECT id FROM users WHERE login_id='owner7') WHERE business_number='1000000007';

-- owner8
INSERT INTO users (role, login_id, password, name, phone, gender, birth, created_at, updated_at)
VALUES ('OWNER', 'owner8', @PW, '알렉사장', '01088888888', 'MALE', '1991-08-08', NOW(), NOW());
INSERT INTO owner_profile (user_id, business_number)
SELECT id, '1000000008' FROM users WHERE login_id='owner8';
UPDATE store SET owner_id = (SELECT id FROM users WHERE login_id='owner8') WHERE business_number='1000000008';

-- owner9
INSERT INTO users (role, login_id, password, name, phone, gender, birth, created_at, updated_at)
VALUES ('OWNER', 'owner9', @PW, 'AS사장', '01099999998', 'MALE', '1990-09-09', NOW(), NOW());
INSERT INTO owner_profile (user_id, business_number)
SELECT id, '1000000009' FROM users WHERE login_id='owner9';
UPDATE store SET owner_id = (SELECT id FROM users WHERE login_id='owner9') WHERE business_number='1000000009';

-- owner10
INSERT INTO users (role, login_id, password, name, phone, gender, birth, created_at, updated_at)
VALUES ('OWNER', 'owner10', @PW, '언노운사장', '01010101010', 'FEMALE', '1994-10-10', NOW(), NOW());
INSERT INTO owner_profile (user_id, business_number)
SELECT id, '1000000010' FROM users WHERE login_id='owner10';
UPDATE store SET owner_id = (SELECT id FROM users WHERE login_id='owner10') WHERE business_number='1000000010';

-- owner11
INSERT INTO users (role, login_id, password, name, phone, gender, birth, created_at, updated_at)
VALUES ('OWNER', 'owner11', @PW, '텟어텟사장', '01011111112', 'FEMALE', '1992-11-11', NOW(), NOW());
INSERT INTO owner_profile (user_id, business_number)
SELECT id, '1000000011' FROM users WHERE login_id='owner11';
UPDATE store SET owner_id = (SELECT id FROM users WHERE login_id='owner11') WHERE business_number='1000000011';

-- owner12
INSERT INTO users (role, login_id, password, name, phone, gender, birth, created_at, updated_at)
VALUES ('OWNER', 'owner12', @PW, '레이어사장', '01012121212', 'MALE', '1995-12-12', NOW(), NOW());
INSERT INTO owner_profile (user_id, business_number)
SELECT id, '1000000012' FROM users WHERE login_id='owner12';
UPDATE store SET owner_id = (SELECT id FROM users WHERE login_id='owner12') WHERE business_number='1000000012';

-- =============================================================================
-- V12: convince (UI chips) + store_convince 매핑 (bulk 스타일)
-- =============================================================================

-- insert convince (UI chips)
INSERT INTO convince (display_name, code) VALUES
  ('간편결제', 'EASY_PAY')
ON DUPLICATE KEY UPDATE
  display_name = VALUES(display_name);


-- -----------------------------------------------------------------------------
-- Store 7~12: options -> store_convince (중간테이블)
-- -----------------------------------------------------------------------------

-- Store 7: 포장, 배달, 간편결제
INSERT IGNORE INTO store_convince (store_id, convince_id)
SELECT 7, c.id FROM convince c
WHERE c.code IN ('TAKEOUT','DELIVERY','EASY_PAY');

-- Store 8: 와이파이, 포장, 배달, 반려동물 동반
INSERT IGNORE INTO store_convince (store_id, convince_id)
SELECT 8, c.id FROM convince c
WHERE c.code IN ('WIFI','TAKEOUT','DELIVERY','PET_FRIENDLY');

-- Store 9: 포장, 배달
INSERT IGNORE INTO store_convince (store_id, convince_id)
SELECT 9, c.id FROM convince c
WHERE c.code IN ('TAKEOUT','DELIVERY');

-- Store 10: 포장, 와이파이, 반려동물 동반, 주차가능
INSERT IGNORE INTO store_convince (store_id, convince_id)
SELECT 10, c.id FROM convince c
WHERE c.code IN ('TAKEOUT','WIFI','PET_FRIENDLY','PARKING');

-- Store 11: 단체석, 포장, 배달, 와이파이
INSERT IGNORE INTO store_convince (store_id, convince_id)
SELECT 11, c.id FROM convince c
WHERE c.code IN ('GROUP_SEAT','TAKEOUT','DELIVERY','WIFI');

-- Store 12: 단체석, 배달, 포장, 와이파이, 예약 가능, 장애인 편의시설(=휠체어)
INSERT IGNORE INTO store_convince (store_id, convince_id)
SELECT 12, c.id FROM convince c
WHERE c.code IN ('GROUP_SEAT','DELIVERY','TAKEOUT','WIFI','RESERVATION','ACCESSIBLE');

-- Optional sanity checks
-- SELECT s.id, s.name, GROUP_CONCAT(c.code ORDER BY c.code SEPARATOR ',') AS convinces
-- FROM store s
-- LEFT JOIN store_convince sc ON sc.store_id = s.id
-- LEFT JOIN convince c ON c.id = sc.convince_id
-- WHERE s.id BETWEEN 7 AND 12
-- GROUP BY s.id, s.name;

-- 3) 메뉴 3개씩 생성 (대표 메뉴)
-- NOTE: type은 기존 스키마의 VARCHAR(15)이므로 'MAIN'으로 통일

-- store 7
INSERT INTO menu (store_id, name, price, sort_order, image, type)
SELECT s.id, '금빛소금커피', 5200, 1, NULL, 'MAIN'
FROM store s WHERE s.business_number='1000000007'
AND NOT EXISTS (SELECT 1 FROM menu m WHERE m.store_id=s.id AND m.sort_order=1);

INSERT INTO menu (store_id, name, price, sort_order, image, type)
SELECT s.id, '아메리카노', 1700, 2, NULL, 'MAIN'
FROM store s WHERE s.business_number='1000000007'
AND NOT EXISTS (SELECT 1 FROM menu m WHERE m.store_id=s.id AND m.sort_order=2);

INSERT INTO menu (store_id, name, price, sort_order, image, type)
SELECT s.id, '온.나 크림라떼', 4900, 3, NULL, 'MAIN'
FROM store s WHERE s.business_number='1000000007'
AND NOT EXISTS (SELECT 1 FROM menu m WHERE m.store_id=s.id AND m.sort_order=3);

-- store 8
INSERT INTO menu (store_id, name, price, sort_order, image, type)
SELECT s.id, '오늘의 커피', 4500, 1, NULL, 'MAIN'
FROM store s WHERE s.business_number='1000000008'
AND NOT EXISTS (SELECT 1 FROM menu m WHERE m.store_id=s.id AND m.sort_order=1);

INSERT INTO menu (store_id, name, price, sort_order, image, type)
SELECT s.id, '롱블랙', 4500, 2, NULL, 'MAIN'
FROM store s WHERE s.business_number='1000000008'
AND NOT EXISTS (SELECT 1 FROM menu m WHERE m.store_id=s.id AND m.sort_order=2);

INSERT INTO menu (store_id, name, price, sort_order, image, type)
SELECT s.id, '코르타도', 4800, 3, NULL, 'MAIN'
FROM store s WHERE s.business_number='1000000008'
AND NOT EXISTS (SELECT 1 FROM menu m WHERE m.store_id=s.id AND m.sort_order=3);

-- store 9
INSERT INTO menu (store_id, name, price, sort_order, image, type)
SELECT s.id, 'AS 시그니쳐 라떼', 3700, 1, NULL, 'MAIN'
FROM store s WHERE s.business_number='1000000009'
AND NOT EXISTS (SELECT 1 FROM menu m WHERE m.store_id=s.id AND m.sort_order=1);

INSERT INTO menu (store_id, name, price, sort_order, image, type)
SELECT s.id, '헛개리카노', 3100, 2, NULL, 'MAIN'
FROM store s WHERE s.business_number='1000000009'
AND NOT EXISTS (SELECT 1 FROM menu m WHERE m.store_id=s.id AND m.sort_order=2);

INSERT INTO menu (store_id, name, price, sort_order, image, type)
SELECT s.id, '윈터 시그니쳐 뱅쇼', 4300, 3, NULL, 'MAIN'
FROM store s WHERE s.business_number='1000000009'
AND NOT EXISTS (SELECT 1 FROM menu m WHERE m.store_id=s.id AND m.sort_order=3);

-- store 10
INSERT INTO menu (store_id, name, price, sort_order, image, type)
SELECT s.id, '딥바닐라빈라떼', 5000, 1, NULL, 'MAIN'
FROM store s WHERE s.business_number='1000000010'
AND NOT EXISTS (SELECT 1 FROM menu m WHERE m.store_id=s.id AND m.sort_order=1);

INSERT INTO menu (store_id, name, price, sort_order, image, type)
SELECT s.id, '아인슈페너', 5300, 2, NULL, 'MAIN'
FROM store s WHERE s.business_number='1000000010'
AND NOT EXISTS (SELECT 1 FROM menu m WHERE m.store_id=s.id AND m.sort_order=2);

INSERT INTO menu (store_id, name, price, sort_order, image, type)
SELECT s.id, '아몬드슈페너', 6000, 3, NULL, 'MAIN'
FROM store s WHERE s.business_number='1000000010'
AND NOT EXISTS (SELECT 1 FROM menu m WHERE m.store_id=s.id AND m.sort_order=3);

-- store 11
INSERT INTO menu (store_id, name, price, sort_order, image, type)
SELECT s.id, 'TAT 크림라떼', 6800, 1, NULL, 'MAIN'
FROM store s WHERE s.business_number='1000000011'
AND NOT EXISTS (SELECT 1 FROM menu m WHERE m.store_id=s.id AND m.sort_order=1);

INSERT INTO menu (store_id, name, price, sort_order, image, type)
SELECT s.id, '복숭아 자두 에이드', 6000, 2, NULL, 'MAIN'
FROM store s WHERE s.business_number='1000000011'
AND NOT EXISTS (SELECT 1 FROM menu m WHERE m.store_id=s.id AND m.sort_order=2);

INSERT INTO menu (store_id, name, price, sort_order, image, type)
SELECT s.id, '팥크림 쑥 라떼', 6500, 3, NULL, 'MAIN'
FROM store s WHERE s.business_number='1000000011'
AND NOT EXISTS (SELECT 1 FROM menu m WHERE m.store_id=s.id AND m.sort_order=3);

-- store 12
INSERT INTO menu (store_id, name, price, sort_order, image, type)
SELECT s.id, '바닐라아메리카노', 5500, 1, NULL, 'MAIN'
FROM store s WHERE s.business_number='1000000012'
AND NOT EXISTS (SELECT 1 FROM menu m WHERE m.store_id=s.id AND m.sort_order=1);

INSERT INTO menu (store_id, name, price, sort_order, image, type)
SELECT s.id, '바닐라라떼', 6300, 2, NULL, 'MAIN'
FROM store s WHERE s.business_number='1000000012'
AND NOT EXISTS (SELECT 1 FROM menu m WHERE m.store_id=s.id AND m.sort_order=2);

INSERT INTO menu (store_id, name, price, sort_order, image, type)
SELECT s.id, '헤이즐럿라떼', 6300, 3, NULL, 'MAIN'
FROM store s WHERE s.business_number='1000000012'
AND NOT EXISTS (SELECT 1 FROM menu m WHERE m.store_id=s.id AND m.sort_order=3);