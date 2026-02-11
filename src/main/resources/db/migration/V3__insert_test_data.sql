-- -----------------------------------------------------------------------------
-- V3__Insert_Test_Data.sql
-- 설명: 초기 가게 정보(6개) 및 가게별 이미지 7장씩 등록
-- -----------------------------------------------------------------------------

-- 1. 카페 레이지아워
INSERT INTO store (name, road_address, open_time, phone, description, category, latitude, longitude, business_number)
VALUES ('카페 레이지아워', '서울 광진구 아차산로33길 68 지하1층', '매일 10:00 - 24:00', '010-8243-9368', '포장 가능, 예약 가능, 화이파이 있음, 반려동물 동반 가능', 'CAFE', 37.5407625, 127.0706095, '1000000001');

INSERT INTO store_image (store_id, image_url, thumbnail_url, sort_order)
SELECT s.id, t.image_url, t.thumbnail_url, t.sort_order
FROM store s
JOIN (
    SELECT 'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/images/LazyHour1.png' AS image_url,
           'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/images/LazyHour1.png' AS thumbnail_url,
           1 AS sort_order
    UNION ALL
    SELECT 'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/images/LazyHour2.jpeg',
           'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/images/LazyHour2.jpeg', 2
    UNION ALL
    SELECT 'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/images/LazyHour3.jpeg',
           'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/images/LazyHour3.jpeg', 3
    UNION ALL
    SELECT 'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/images/LazyHour4.png',
           'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/images/LazyHour4.png', 4
    UNION ALL
    SELECT 'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/images/LazyHourMenu1.jpeg',
           'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/images/LazyHourMenu1.jpeg', 5
    UNION ALL
    SELECT 'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/images/LazyHourMenu2.jpeg',
           'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/images/LazyHourMenu2.jpeg', 6
    UNION ALL
    SELECT 'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/images/LazyHourMenu3.jpeg',
           'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/images/LazyHourMenu3.jpeg', 7
) t
WHERE s.business_number = '1000000001';


-- 2. 마이 디어 버터하우스
INSERT INTO store (name, road_address, open_time, phone, description, category, latitude, longitude, business_number)
VALUES ('마이 디어 버터하우스', '서울 광진구 능동로13길 74 2층', '매일 12:00 - 22:00', '0507-1331-9074', '와이파이 있음, 단체석 있음, 반려동물 동반 가능', 'CAFE', 37.5407625, 127.0706095, '1000000002');

INSERT INTO store_image (store_id, image_url, thumbnail_url, sort_order)
SELECT s.id, t.image_url, t.thumbnail_url, t.sort_order
FROM store s
JOIN (
    SELECT 'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/images/MyDearButterHouse1.png' AS image_url,
           'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/images/MyDearButterHouse1.png' AS thumbnail_url,
           1 AS sort_order
    UNION ALL
    SELECT 'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/images/MyDearButterHouse2.jpeg',
           'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/images/MyDearButterHouse2.jpeg', 2
    UNION ALL
    SELECT 'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/images/MyDearButterHouse3.png',
           'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/images/MyDearButterHouse3.png', 3
    UNION ALL
    SELECT 'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/images/MyDearButterHouse4.jpeg',
           'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/images/MyDearButterHouse4.jpeg', 4
    UNION ALL
    SELECT 'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/images/MyDearButterHouseMenu1.png',
           'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/images/MyDearButterHouseMenu1.png', 5
    UNION ALL
    SELECT 'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/images/MyDearButterHouseMenu2.png',
           'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/images/MyDearButterHouseMenu2.png', 6
    UNION ALL
    SELECT 'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/images/MyDearButterHouseMenu3.png',
           'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/images/MyDearButterHouseMenu3.png', 7
) t
WHERE s.business_number = '1000000002';


-- 3. 도우터
INSERT INTO store (name, road_address, open_time, phone, description, category, latitude, longitude, business_number)
VALUES ('도우터', '서울 광진구 아차산로31길 28 1층', '매일 11:00 - 22:30', '0507-1478-0559', '단체석 있음, 와이파이 있음, 데이트하기 좋은', 'CAFE', 37.5407625, 127.0706095, '1000000003');

INSERT INTO store_image (store_id, image_url, thumbnail_url, sort_order)
SELECT s.id, t.image_url, t.thumbnail_url, t.sort_order
FROM store s
JOIN (
    SELECT 'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/images/Daughter1.jpeg' AS image_url,
           'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/images/Daughter1.jpeg' AS thumbnail_url,
           1 AS sort_order
    UNION ALL
    SELECT 'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/images/Daughter2.jpeg',
           'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/images/Daughter2.jpeg', 2
    UNION ALL
    SELECT 'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/images/Daughter3.png',
           'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/images/Daughter3.png', 3
    UNION ALL
    SELECT 'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/images/Daughter4.png',
           'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/images/Daughter4.png', 4
    UNION ALL
    SELECT 'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/images/DaughterMenu1.png',
           'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/images/DaughterMenu1.png', 5
    UNION ALL
    SELECT 'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/images/DaughterMenu2.png',
           'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/images/DaughterMenu2.png', 6
    UNION ALL
    SELECT 'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/images/DaughterMenu3.jpeg',
           'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/images/DaughterMenu3.jpeg', 7
) t
WHERE s.business_number = '1000000003';


-- 4. café 462
INSERT INTO store (name, road_address, open_time, phone, description, category, latitude, longitude, business_number)
VALUES ('café 462', '서울 광진구 동일로22길 117-17 1층', '매일 12:00 - 20:30', '0507-1341-8216', '포장 가능, 예약 가능, 와이파이 있음, 수제로 완성한 케이크', 'CAFE', 37.5407625, 127.0706095, '1000000004');

INSERT INTO store_image (store_id, image_url, thumbnail_url, sort_order)
SELECT s.id, t.image_url, t.thumbnail_url, t.sort_order
FROM store s
JOIN (
    SELECT 'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/images/Cafe462_1.jpeg' AS image_url,
           'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/images/Cafe462_1.jpeg' AS thumbnail_url,
           1 AS sort_order
    UNION ALL
    SELECT 'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/images/Cafe462_2.jpeg',
           'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/images/Cafe462_2.jpeg', 2
    UNION ALL
    SELECT 'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/images/Cafe462_3.jpeg',
           'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/images/Cafe462_3.jpeg', 3
    UNION ALL
    SELECT 'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/images/Cafe462_4.jpeg',
           'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/images/Cafe462_4.jpeg', 4
    UNION ALL
    SELECT 'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/images/Cafe462Menu_1.jpeg',
           'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/images/Cafe462Menu_1.jpeg', 5
    UNION ALL
    SELECT 'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/images/Cafe462Menu_2.jpeg',
           'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/images/Cafe462Menu_2.jpeg', 6
    UNION ALL
    SELECT 'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/images/Cafe462Menu_3.jpeg',
           'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/images/Cafe462Menu_3.jpeg', 7
) t
WHERE s.business_number = '1000000004';


-- 5. 카페 언필드
INSERT INTO store (name, road_address, open_time, phone, description, category, latitude, longitude, business_number)
VALUES ('카페 언필드', '서울 광진구 동일로22길 96 1층', '매일 12:00 - 22:00', '0507-1374-6750', '포장 가능, 무선인터넷, 시원한 망고빙수로 여름나기, 디저트 카페', 'CAFE', 37.5407625, 127.0706095, '1000000005');

INSERT INTO store_image (store_id, image_url, thumbnail_url, sort_order)
SELECT s.id, t.image_url, t.thumbnail_url, t.sort_order
FROM store s
JOIN (
    SELECT 'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/images/Unfield1.jpeg' AS image_url,
           'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/images/Unfield1.jpeg' AS thumbnail_url,
           1 AS sort_order
    UNION ALL
    SELECT 'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/images/Unfield2.jpeg',
           'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/images/Unfield2.jpeg', 2
    UNION ALL
    SELECT 'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/images/Unfield3.png',
           'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/images/Unfield3.png', 3
    UNION ALL
    SELECT 'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/images/Unfield4.png',
           'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/images/Unfield4.png', 4
    UNION ALL
    SELECT 'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/images/UnfieldMenu1.png',
           'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/images/UnfieldMenu1.png', 5
    UNION ALL
    SELECT 'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/images/UnfieldMenu2.png',
           'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/images/UnfieldMenu2.png', 6
    UNION ALL
    SELECT 'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/images/UnfieldMenu3.jpeg',
           'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/images/UnfieldMenu3.jpeg', 7
) t
WHERE s.business_number = '1000000005';


-- 6. 더이퀄리브리엄커피
INSERT INTO store (name, road_address, open_time, phone, description, category, latitude, longitude, business_number)
VALUES ('더이퀄리브리엄커피', '서울 광진구 아차산로30길 7 3층 더이퀄리브리엄커피', '매일 12:00 - 22:00', '0507-1332-6073', '와이파이 있음, 포장 가능, 반려동물과 함께하는 편안한 카페 체험', 'CAFE', 37.5407625, 127.0706095, '1000000006');

INSERT INTO store_image (store_id, image_url, thumbnail_url, sort_order)
SELECT s.id, t.image_url, t.thumbnail_url, t.sort_order
FROM store s
JOIN (
    SELECT 'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/images/TheEqua1.jpeg' AS image_url,
           'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/images/TheEqua1.jpeg' AS thumbnail_url,
           1 AS sort_order
    UNION ALL
    SELECT 'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/images/TheEqua2.jpeg',
           'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/images/TheEqua2.jpeg', 2
    UNION ALL
    SELECT 'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/images/TheEqua3.jpeg',
           'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/images/TheEqua3.jpeg', 3
    UNION ALL
    SELECT 'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/images/TheEqua4.jpeg',
           'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/images/TheEqua4.jpeg', 4
    UNION ALL
    SELECT 'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/images/TheEqualMenu1.png',
           'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/images/TheEqualMenu1.png', 5
    UNION ALL
    SELECT 'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/images/TheEqualMenu2.jpeg',
           'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/images/TheEqualMenu2.jpeg', 6
    UNION ALL
    SELECT 'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/images/TheEqualMenu3.jpg',
           'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/images/TheEqualMenu3.jpg', 7
) t
WHERE s.business_number = '1000000006';