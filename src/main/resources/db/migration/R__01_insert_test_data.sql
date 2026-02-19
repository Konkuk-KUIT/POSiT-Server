-- -----------------------------------------------------------------------------
-- V3__Insert_Test_Data.sql
-- 설명: 초기 가게 정보(6개) 및 가게별 이미지 4장씩 등록
-- -----------------------------------------------------------------------------

-- 1. 카페 레이지아워
INSERT INTO store (name, road_address, open_time, phone, description, category, latitude, longitude, business_number, not_open, lot_address)
VALUES ('카페 레이지아워', '서울특별시 광진구 아차산로33길 68 지하1층 레이지아워', '10:00-00:00', '010-8243-9368', '에그타르트 한 입에 느껴지는 고소함', 'CAFE', 37.543142, 127.071267, '1000000001', null, '화양동 3-75');

INSERT INTO store_image (store_id, image_url, thumbnail_url, sort_order)
SELECT s.id, t.image_url, t.thumbnail_url, t.sort_order
FROM store s
JOIN (
    SELECT 'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/uploads/stores/LazyHour1.png' AS image_url,
           'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/uploads/stores/LazyHour1.png' AS thumbnail_url,
           1 AS sort_order
    UNION ALL
    SELECT 'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/uploads/stores/LazyHour2.jpeg',
           'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/uploads/stores/LazyHour2.jpeg', 2
    UNION ALL
    SELECT 'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/uploads/stores/LazyHour3.jpeg',
           'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/uploads/stores/LazyHour3.jpeg', 3
    UNION ALL
    SELECT 'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/uploads/stores/LazyHour4.png',
           'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/uploads/stores/LazyHour4.png', 4
) t
WHERE s.business_number = '1000000001';


-- 2. 마이 디어 버터하우스
INSERT INTO store (name, road_address, open_time, phone, description, category, latitude, longitude, business_number, not_open, lot_address)
VALUES ('마이 디어 버터하우스', '서울 광진구 능동로13길 74 1층', '12:00-22:00', '0507-1331-9074', '아늑한 카페에서 즐기는 휘낭시에 한입', 'CAFE',  37.544937, 127.069129, '1000000002', null, '서울 광진구 화양동 16-37');

INSERT INTO store_image (store_id, image_url, thumbnail_url, sort_order)
SELECT s.id, t.image_url, t.thumbnail_url, t.sort_order
FROM store s
JOIN (
    SELECT 'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/uploads/stores/MyDearButterHouse1.png' AS image_url,
           'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/uploads/stores/MyDearButterHouse1.png' AS thumbnail_url,
           1 AS sort_order
    UNION ALL
    SELECT 'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/uploads/stores/MyDearButterHouse2.jpeg',
           'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/uploads/stores/MyDearButterHouse2.jpeg', 2
    UNION ALL
    SELECT 'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/uploads/stores/MyDearButterHouse3.png',
           'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/uploads/stores/MyDearButterHouse3.png', 3
    UNION ALL
    SELECT 'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/uploads/stores/MyDearButterHouse4.jpeg',
           'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/uploads/stores/MyDearButterHouse4.jpeg', 4
) t
WHERE s.business_number = '1000000002';


-- 3. 도우터
INSERT INTO store (name, road_address, open_time, phone, description, category, latitude, longitude, business_number, not_open, lot_address)
VALUES ('도우터', '서울특별시 광진구 아차산로31길 40 1층', '11:00-22:30', '0507-1478-0559', '데이트하기 좋은 감성 가득한 공간', 'CAFE', 37.542688, 127.070172, '1000000003', null, '화양동 11-17');

INSERT INTO store_image (store_id, image_url, thumbnail_url, sort_order)
SELECT s.id, t.image_url, t.thumbnail_url, t.sort_order
FROM store s
JOIN (
    SELECT 'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/uploads/stores/Daughter1.jpeg' AS image_url,
           'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/uploads/stores/Daughter1.jpeg' AS thumbnail_url,
           1 AS sort_order
    UNION ALL
    SELECT 'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/uploads/stores/Daughter2.jpeg',
           'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/uploads/stores/Daughter2.jpeg', 2
    UNION ALL
    SELECT 'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/uploads/stores/Daughter3.png',
           'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/uploads/stores/Daughter3.png', 3
    UNION ALL
    SELECT 'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/uploads/stores/Daughter4.png',
           'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/uploads/stores/Daughter4.png', 4
) t
WHERE s.business_number = '1000000003';


-- 4. cafe 462
INSERT INTO store (name, road_address, open_time, phone, description, category, latitude, longitude, business_number, not_open, lot_address)
VALUES ('cafe 462', '서울 광진구 동일로24길 54 1층', '12:00-20:30', '0507-1341-8216', '수제로 완성한 케이크의 진한 매력', 'CAFE', 37.543130, 127.067928, '1000000004', null, '서울 광진구 화양동 46-2');

INSERT INTO store_image (store_id, image_url, thumbnail_url, sort_order)
SELECT s.id, t.image_url, t.thumbnail_url, t.sort_order
FROM store s
JOIN (
    SELECT 'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/uploads/stores/Cafe462_1.jpeg' AS image_url,
           'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/uploads/stores/Cafe462_1.jpeg' AS thumbnail_url,
           1 AS sort_order
    UNION ALL
    SELECT 'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/uploads/stores/Cafe462_2.jpeg',
           'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/uploads/stores/Cafe462_2.jpeg', 2
    UNION ALL
    SELECT 'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/uploads/stores/Cafe462_3.jpeg',
           'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/uploads/stores/Cafe462_3.jpeg', 3
    UNION ALL
    SELECT 'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/uploads/stores/Cafe462_4.jpeg',
           'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/uploads/stores/Cafe462_4.jpeg', 4
) t
WHERE s.business_number = '1000000004';


-- 5. 카페 언필드
INSERT INTO store (name, road_address, open_time, phone, description, category, latitude, longitude, business_number, not_open, lot_address)
VALUES ('카페 언필드', '서울 광진구 동일로22길 30 2층', '12:00-22:00', '0507-1374-6750', '시원한 망고빙수로 여름 극복', 'CAFE', 37.542059, 127.065919, '1000000005', 'TUE','서울 광진구 화양동 49-15');

INSERT INTO store_image (store_id, image_url, thumbnail_url, sort_order)
SELECT s.id, t.image_url, t.thumbnail_url, t.sort_order
FROM store s
JOIN (
    SELECT 'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/uploads/stores/Unfield1.jpeg' AS image_url,
           'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/uploads/stores/Unfield1.jpeg' AS thumbnail_url,
           1 AS sort_order
    UNION ALL
    SELECT 'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/uploads/stores/Unfield2.jpeg',
           'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/uploads/stores/Unfield2.jpeg', 2
    UNION ALL
    SELECT 'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/uploads/stores/Unfield3.png',
           'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/uploads/stores/Unfield3.png', 3
    UNION ALL
    SELECT 'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/uploads/stores/Unfield4.png',
           'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/uploads/stores/Unfield4.png', 4
) t
WHERE s.business_number = '1000000005';


-- 6. 더이퀄리브리엄커피
INSERT INTO store (name, road_address, open_time, phone, description, category, latitude, longitude, business_number, not_open, lot_address)
VALUES ('더이퀄리브리엄커피', '서울 광진구 아차산로30길 7 3층 더이퀄리브리엄커피', '12:00-22:00', '0507-1332-6073', '반려동물과 함께하는 편안한 카페 체험', 'CAFE',  37.540354, 127.067831, '1000000006', 'TUE','서울 광진구 자양동 7-30');

INSERT INTO store_image (store_id, image_url, thumbnail_url, sort_order)
SELECT s.id, t.image_url, t.thumbnail_url, t.sort_order
FROM store s
JOIN (
    SELECT 'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/uploads/stores/TheEqual1.jpeg' AS image_url,
           'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/uploads/stores/TheEqual1.jpeg' AS thumbnail_url,
           1 AS sort_order
    UNION ALL
    SELECT 'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/uploads/stores/TheEqual2.jpeg',
           'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/uploads/stores/TheEqual2.jpeg', 2
    UNION ALL
    SELECT 'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/uploads/stores/TheEqual3.jpeg',
           'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/uploads/stores/TheEqual3.jpeg', 3
    UNION ALL
    SELECT 'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/uploads/stores/TheEqual4.jpeg',
           'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/uploads/stores/TheEqual4.jpeg', 4
) t
WHERE s.business_number = '1000000006';

-- ============================================================
-- 설명: 사장님 계정 일괄 생성 (테스트 계정 1개 + 가게별 계정 6개)
-- ============================================================

-- 2. 카페 레이지아워 사장님 (owner1) -> 1000000001
INSERT INTO users (role, login_id, password, name, phone, gender, birth, created_at, updated_at)
VALUES ('OWNER', 'owner1', '$2a$10$T0kcl6QQcMc5oDMdOYvIRecSCH179zPaLY57Y/uEhQoOy93U8qyRO', '레이지사장', '01011111111', 'FEMALE', '1995-01-01', NOW(), NOW());

INSERT INTO owner_profile (user_id, business_number)
SELECT id, '1000000001' FROM users WHERE login_id = 'owner1';

UPDATE store SET owner_id = (SELECT id FROM users WHERE login_id = 'owner1') WHERE business_number = '1000000001';


-- 3. 마이 디어 버터하우스 사장님 (owner2) -> 1000000002
INSERT INTO users (role, login_id, password, name, phone, gender, birth, created_at, updated_at)
VALUES ('OWNER', 'owner2', '$2a$10$T0kcl6QQcMc5oDMdOYvIRecSCH179zPaLY57Y/uEhQoOy93U8qyRO', '버터사장님', '01022222222', 'FEMALE', '1993-05-05', NOW(), NOW());

INSERT INTO owner_profile (user_id, business_number)
SELECT id, '1000000002' FROM users WHERE login_id = 'owner2';

UPDATE store SET owner_id = (SELECT id FROM users WHERE login_id = 'owner2') WHERE business_number = '1000000002';


-- 4. 도우터 사장님 (owner3) -> 1000000003
INSERT INTO users (role, login_id, password, name, phone, gender, birth, created_at, updated_at)
VALUES ('OWNER', 'owner3', '$2a$10$T0kcl6QQcMc5oDMdOYvIRecSCH179zPaLY57Y/uEhQoOy93U8qyRO', '도우터사장', '01033333333', 'MALE', '1988-08-08', NOW(), NOW());

INSERT INTO owner_profile (user_id, business_number)
SELECT id, '1000000003' FROM users WHERE login_id = 'owner3';

UPDATE store SET owner_id = (SELECT id FROM users WHERE login_id = 'owner3') WHERE business_number = '1000000003';


-- 5. café 462 사장님 (owner4) -> 1000000004
INSERT INTO users (role, login_id, password, name, phone, gender, birth, created_at, updated_at)
VALUES ('OWNER', 'owner4', '$2a$10$T0kcl6QQcMc5oDMdOYvIRecSCH179zPaLY57Y/uEhQoOy93U8qyRO', '462사장님', '01044444444', 'FEMALE', '1990-12-25', NOW(), NOW());

INSERT INTO owner_profile (user_id, business_number)
SELECT id, '1000000004' FROM users WHERE login_id = 'owner4';

UPDATE store SET owner_id = (SELECT id FROM users WHERE login_id = 'owner4') WHERE business_number = '1000000004';


-- 6. 카페 언필드 사장님 (owner5) -> 1000000005
INSERT INTO users (role, login_id, password, name, phone, gender, birth, created_at, updated_at)
VALUES ('OWNER', 'owner5', '$2a$10$T0kcl6QQcMc5oDMdOYvIRecSCH179zPaLY57Y/uEhQoOy93U8qyRO', '언필드사장', '01055555555', 'MALE', '1996-06-06', NOW(), NOW());

INSERT INTO owner_profile (user_id, business_number)
SELECT id, '1000000005' FROM users WHERE login_id = 'owner5';

UPDATE store SET owner_id = (SELECT id FROM users WHERE login_id = 'owner5') WHERE business_number = '1000000005';


-- 7. 더이퀄리브리엄커피 사장님 (owner6) -> 1000000006
INSERT INTO users (role, login_id, password, name, phone, gender, birth, created_at, updated_at)
VALUES ('OWNER', 'owner6', '$2a$10$T0kcl6QQcMc5oDMdOYvIRecSCH179zPaLY57Y/uEhQoOy93U8qyRO', '이퀄리사장', '01066666666', 'MALE', '1992-02-02', NOW(), NOW());

INSERT INTO owner_profile (user_id, business_number)
SELECT id, '1000000006' FROM users WHERE login_id = 'owner6';

UPDATE store SET owner_id = (SELECT id FROM users WHERE login_id = 'owner6') WHERE business_number = '1000000006';



-- 3) Seed TYPE filters (store type chips) and map stores via store_filter
INSERT INTO filter (category, code, display_name) VALUES
                                                      ('TYPE', 'STUDY',   '스터디 카페'),
                                                      ('TYPE', 'BRUNCH',  '브런치 카페'),
                                                      ('TYPE', 'DESSERT', '디저트 카페')
    ON DUPLICATE KEY UPDATE
                         display_name = VALUES(display_name);

SET @s1 := (SELECT id FROM store WHERE business_number = '1000000001');
SET @s2 := (SELECT id FROM store WHERE business_number = '1000000002');
SET @s3 := (SELECT id FROM store WHERE business_number = '1000000003');
SET @s4 := (SELECT id FROM store WHERE business_number = '1000000004');
SET @s5 := (SELECT id FROM store WHERE business_number = '1000000005');
SET @s6 := (SELECT id FROM store WHERE business_number = '1000000006');

-- Map store -> TYPE filter
INSERT IGNORE INTO store_filter (store_id, filter_id)
SELECT @s1, f.id FROM filter f WHERE f.category='TYPE' AND f.code='DESSERT';

INSERT IGNORE INTO store_filter (store_id, filter_id)
SELECT @s2, f.id FROM filter f WHERE f.category='TYPE' AND f.code='STUDY';

INSERT IGNORE INTO store_filter (store_id, filter_id)
SELECT @s3, f.id FROM filter f WHERE f.category='TYPE' AND f.code='BRUNCH';

INSERT IGNORE INTO store_filter (store_id, filter_id)
SELECT @s4, f.id FROM filter f WHERE f.category='TYPE' AND f.code='DESSERT';

INSERT IGNORE INTO store_filter (store_id, filter_id)
SELECT @s5, f.id FROM filter f WHERE f.category='TYPE' AND f.code='BRUNCH';

INSERT IGNORE INTO store_filter (store_id, filter_id)
SELECT @s6, f.id FROM filter f WHERE f.category='TYPE' AND f.code='STUDY';

-- insert convince (UI chips)
INSERT INTO convince (display_name, code) VALUES
                                              ('포장 가능', 'TAKEOUT'),
                                              ('배달 가능', 'DELIVERY'),
                                              ('예약 가능', 'RESERVATION'),
                                              ('24시간 영업', 'OPEN_24H'),

                                              ('주차 가능', 'PARKING'),
                                              ('발렛 파킹', 'VALET_PARKING'),
                                              ('장애인 편의시설', 'ACCESSIBLE'),

                                              ('반려동물 동반 가능', 'PET_FRIENDLY'),
                                              ('노키즈존', 'NO_KIDS'),

                                              ('와이파이 있음', 'WIFI'),
                                              ('단체석 있음', 'GROUP_SEAT'),
                                              ('룸 있음', 'PRIVATE_ROOM'),
                                              ('흡연실 있음', 'SMOKING_ROOM'),
                                              ('간편결제', "EASY_PAY"),
                                              ('야외 좌석', 'OUTDOOR_SEAT')

    ON DUPLICATE KEY UPDATE
display_name = VALUES(display_name);


-- convince
INSERT IGNORE INTO store_convince (store_id, convince_id)
SELECT @s1, c.id FROM convince c
WHERE c.code IN ('TAKEOUT','RESERVATION','WIFI','PET_FRIENDLY');

-- Store 2: WIFI, GROUP_SEAT, TAKEOUT, DELIVERY
INSERT IGNORE INTO store_convince (store_id, convince_id)
SELECT @s2, c.id FROM convince c
WHERE c.code IN ('WIFI','GROUP_SEAT','TAKEOUT','DELIVERY');

-- Store 3: GROUP_SEAT, WIFI, TAKEOUT
INSERT IGNORE INTO store_convince (store_id, convince_id)
SELECT @s3, c.id FROM convince c
WHERE c.code IN ('GROUP_SEAT','WIFI','TAKEOUT');

-- Store 4: TAKEOUT, RESERVATION, WIFI
INSERT IGNORE INTO store_convince (store_id, convince_id)
SELECT @s4, c.id FROM convince c
WHERE c.code IN ('TAKEOUT','RESERVATION','WIFI');

-- Store 5: TAKEOUT, WIFI, DELIVERY, GROUP_SEAT
-- Excel source had a typo like "무와이파이"; we normalize to WIFI.
INSERT IGNORE INTO store_convince (store_id, convince_id)
SELECT @s5, c.id FROM convince c
WHERE c.code IN ('TAKEOUT','WIFI','DELIVERY','GROUP_SEAT');

-- Store 6: WIFI, TAKEOUT
INSERT IGNORE INTO store_convince (store_id, convince_id)
SELECT @s6, c.id FROM convince c
WHERE c.code IN ('WIFI','TAKEOUT');

-- 1) (선택) 메뉴 데이터가 아직 없다면 6개 매장 x 3개 메뉴를 넣는다.
--    이미 들어가 있으면 NOT EXISTS로 스킵됨.
-- 레이지아워
INSERT INTO menu (store_id, name, price, sort_order, image, type)
SELECT @s1, '솔티 아인슈페너', 4800, 1, 'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/uploads/menu/LazyHourMenu1.jpeg', 'MAIN' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM menu WHERE store_id = @s1 AND sort_order = 1);
INSERT INTO menu (store_id, name, price, sort_order, image, type)
SELECT @s1, '뺑 오 스위스', 6000, 2, 'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/uploads/menu/LazyHourMenu2.jpeg', 'MAIN' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM menu WHERE store_id = @s1 AND sort_order = 2);
INSERT INTO menu (store_id, name, price, sort_order, image, type)
SELECT @s1, '에그타르트', 4000, 3, 'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/uploads/menu/LazyHourMenu3.jpeg', 'MAIN' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM menu WHERE store_id = @s1 AND sort_order = 3);

-- 마이디어버터하우스
INSERT INTO menu (store_id, name, price, sort_order, image, type)
SELECT @s2, '판나코타', 6000, 1, 'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/uploads/menu/MyDearButterHouseMenu1.png', 'MAIN' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM menu WHERE store_id = @s2 AND sort_order = 1);
INSERT INTO menu (store_id, name, price, sort_order, image, type)
SELECT @s2, '하트 티라미수', 7000, 2, 'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/uploads/menu/MyDearButterHouseMenu2.png', 'MAIN' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM menu WHERE store_id = @s2 AND sort_order = 2);
INSERT INTO menu (store_id, name, price, sort_order, image, type)
SELECT @s2, '휘낭시에', 3800, 3, 'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/uploads/menu/MyDearButterHouseMenu3.png', 'MAIN' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM menu WHERE store_id = @s2 AND sort_order = 3);

-- 도우터
INSERT INTO menu (store_id, name, price, sort_order, image, type)
SELECT @s3, '연어 차지키 샤워도우', 26000, 1, 'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/uploads/menu/DaughterMenu1.png', 'MAIN' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM menu WHERE store_id = @s3 AND sort_order = 1);
INSERT INTO menu (store_id, name, price, sort_order, image, type)
SELECT @s3, '브런치 플레이트', 23000, 2, 'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/uploads/menu/DaughterMenu2.png', 'MAIN' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM menu WHERE store_id = @s3 AND sort_order = 2);
INSERT INTO menu (store_id, name, price, sort_order, image, type)
SELECT @s3, '씨리얼프렌치 토스트 브리오슈', 15000, 3, 'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/uploads/menu/DaughterMenu3.png', 'MAIN' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM menu WHERE store_id = @s3 AND sort_order = 3);

-- cafe 462
INSERT INTO menu (store_id, name, price, sort_order, image, type)
SELECT @s4, '462판단라떼', 5000, 1, 'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/uploads/menu/Cafe462Menu_1.jpeg', 'MAIN' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM menu WHERE store_id = @s4 AND sort_order = 1);
INSERT INTO menu (store_id, name, price, sort_order, image, type)
SELECT @s4, '빅토리아케이크', 9500, 2, 'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/uploads/menu/Cafe462Menu_2.jpeg', 'MAIN' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM menu WHERE store_id = @s4 AND sort_order = 2);
INSERT INTO menu (store_id, name, price, sort_order, image, type)
SELECT @s4, '체리베리케이크', 8500, 3, 'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/uploads/menu/Cafe462Menu_3.jpeg', 'MAIN' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM menu WHERE store_id = @s4 AND sort_order = 3);

-- 카페 언필드
INSERT INTO menu (store_id, name, price, sort_order, image, type)
SELECT @s5, '프렌치토스트', 8200, 1, 'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/uploads/menu/UnfieldMenu1.png', 'MAIN' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM menu WHERE store_id = @s5 AND sort_order = 1);
INSERT INTO menu (store_id, name, price, sort_order, image, type)
SELECT @s5, '바닐라카라멜 푸딩', 5800, 2, 'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/uploads/menu/UnfieldMenu2.png', 'MAIN' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM menu WHERE store_id = @s5 AND sort_order = 2);
INSERT INTO menu (store_id, name, price, sort_order, image, type)
SELECT @s5, '브륄레치즈케이크', 6900, 3, 'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/uploads/menu/UnfieldMenu3.jpeg', 'MAIN' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM menu WHERE store_id = @s5 AND sort_order = 3);

-- 더이퀄브리엄커피
INSERT INTO menu (store_id, name, price, sort_order, image, type)
SELECT @s6, '수제 밀크티', 9000, 1, 'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/uploads/menu/TheEqualMenu1.png', 'MAIN' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM menu WHERE store_id = @s6 AND sort_order = 1);
INSERT INTO menu (store_id, name, price, sort_order, image, type)
SELECT @s6, '화이트슈페너', 6500, 2, 'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/uploads/menu/TheEqualMenu2.jpeg', 'MAIN' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM menu WHERE store_id = @s6 AND sort_order = 2);
INSERT INTO menu (store_id, name, price, sort_order, image, type)
SELECT @s6, '트와일라잇', 8000, 3, 'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/uploads/menu/TheEqualMenu3.jpg', 'MAIN' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM menu WHERE store_id = @s6 AND sort_order = 3);
