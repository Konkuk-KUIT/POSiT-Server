-- -----------------------------------------------------------------------------
-- V11__fix_image_url.sql
-- 목적:
--  1) store_image: /images/ -> /uploads/stores/
--  2) store_image에 섞인 Menu 이미지는 menu.image로 이관 후 store_image에서 제거
-- 전제:
--  - S3에서 실제 파일을 /uploads/stores/, /uploads/menu/ 로 이미 이동 완료
--  - 파일명은 그대로 유지 (LazyHour1.png, LazyHourMenu1.jpeg 등)
-- -----------------------------------------------------------------------------

-- 1) (선택) 메뉴 데이터가 아직 없다면 6개 매장 x 3개 메뉴를 넣는다.
--    이미 들어가 있으면 NOT EXISTS로 스킵됨.
INSERT INTO menu (store_id, name, price, sort_order, image, type)
SELECT 1, '솔티 아인슈페너', 4800, 1, NULL, 'MAIN' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM menu WHERE store_id = 1 AND sort_order = 1);
INSERT INTO menu (store_id, name, price, sort_order, image, type)
SELECT 1, '뺑 오 스위스', 6000, 2, NULL, 'MAIN' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM menu WHERE store_id = 1 AND sort_order = 2);
INSERT INTO menu (store_id, name, price, sort_order, image, type)
SELECT 1, '에그타르트', 4000, 3, NULL, 'MAIN' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM menu WHERE store_id = 1 AND sort_order = 3);

INSERT INTO menu (store_id, name, price, sort_order, image, type)
SELECT 2, '판나코타', 6000, 1, NULL, 'MAIN' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM menu WHERE store_id = 2 AND sort_order = 1);
INSERT INTO menu (store_id, name, price, sort_order, image, type)
SELECT 2, '하트 티라미수', 7000, 2, NULL, 'MAIN' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM menu WHERE store_id = 2 AND sort_order = 2);
INSERT INTO menu (store_id, name, price, sort_order, image, type)
SELECT 2, '휘낭시에', 3800, 3, NULL, 'MAIN' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM menu WHERE store_id = 2 AND sort_order = 3);

INSERT INTO menu (store_id, name, price, sort_order, image, type)
SELECT 3, '연어 차지키 샤워도우', 26000, 1, NULL, 'MAIN' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM menu WHERE store_id = 3 AND sort_order = 1);
INSERT INTO menu (store_id, name, price, sort_order, image, type)
SELECT 3, '브런치 플레이트', 23000, 2, NULL, 'MAIN' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM menu WHERE store_id = 3 AND sort_order = 2);
INSERT INTO menu (store_id, name, price, sort_order, image, type)
SELECT 3, '씨리얼프렌치 토스트 브리오슈', 15000, 3, NULL, 'MAIN' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM menu WHERE store_id = 3 AND sort_order = 3);

INSERT INTO menu (store_id, name, price, sort_order, image, type)
SELECT 4, '462판단라떼', 5000, 1, NULL, 'MAIN' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM menu WHERE store_id = 4 AND sort_order = 1);
INSERT INTO menu (store_id, name, price, sort_order, image, type)
SELECT 4, '빅토리아케이크', 9500, 2, NULL, 'MAIN' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM menu WHERE store_id = 4 AND sort_order = 2);
INSERT INTO menu (store_id, name, price, sort_order, image, type)
SELECT 4, '체리베리케이크', 8500, 3, NULL, 'MAIN' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM menu WHERE store_id = 4 AND sort_order = 3);

INSERT INTO menu (store_id, name, price, sort_order, image, type)
SELECT 5, '프렌치토스트', 8200, 1, NULL, 'MAIN' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM menu WHERE store_id = 5 AND sort_order = 1);
INSERT INTO menu (store_id, name, price, sort_order, image, type)
SELECT 5, '바닐라카라멜 푸딩', 5800, 2, NULL, 'MAIN' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM menu WHERE store_id = 5 AND sort_order = 2);
INSERT INTO menu (store_id, name, price, sort_order, image, type)
SELECT 5, '브륄레치즈케이크', 6900, 3, NULL, 'MAIN' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM menu WHERE store_id = 5 AND sort_order = 3);

INSERT INTO menu (store_id, name, price, sort_order, image, type)
SELECT 6, '수제 밀크티', 9000, 1, NULL, 'MAIN' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM menu WHERE store_id = 6 AND sort_order = 1);
INSERT INTO menu (store_id, name, price, sort_order, image, type)
SELECT 6, '화이트슈페너', 6500, 2, NULL, 'MAIN' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM menu WHERE store_id = 6 AND sort_order = 2);
INSERT INTO menu (store_id, name, price, sort_order, image, type)
SELECT 6, '트와일라잇', 8000, 3, NULL, 'MAIN' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM menu WHERE store_id = 6 AND sort_order = 3);


-- 2) store_image의 Menu 이미지를 menu.image로 이관
--    (파일명에 Menu1/2/3 포함되어 있으니 그걸로 정확 연결)
UPDATE menu m
    JOIN store_image si ON si.store_id = m.store_id
    SET m.image = REPLACE(si.image_url, '/images/', '/uploads/menu/')
WHERE m.store_id IN (1,2,3,4,5,6)
  AND m.sort_order = 1
  AND si.image_url LIKE '%Menu1%'
  AND (m.image IS NULL OR m.image = '');

UPDATE menu m
    JOIN store_image si ON si.store_id = m.store_id
    SET m.image = REPLACE(si.image_url, '/images/', '/uploads/menu/')
WHERE m.store_id IN (1,2,3,4,5,6)
  AND m.sort_order = 2
  AND si.image_url LIKE '%Menu2%'
  AND (m.image IS NULL OR m.image = '');

UPDATE menu m
    JOIN store_image si ON si.store_id = m.store_id
    SET m.image = REPLACE(si.image_url, '/images/', '/uploads/menu/')
WHERE m.store_id IN (1,2,3,4,5,6)
  AND m.sort_order = 3
  AND (si.image_url LIKE '%Menu3%' OR si.image_url LIKE '%Menu_3%')
  AND (m.image IS NULL OR m.image = '');


-- 3) store_image에서 Menu 이미지는 제거
DELETE FROM store_image
WHERE store_id IN (1,2,3,4,5,6)
  AND image_url LIKE '%Menu%';


-- 4) 남아있는 store_image(=가게 사진) 경로를 /uploads/stores/로 치환
UPDATE store_image
SET image_url = REPLACE(image_url, '/images/', '/uploads/stores/'),
    thumbnail_url = REPLACE(thumbnail_url, '/images/', '/uploads/stores/')
WHERE store_id IN (1,2,3,4,5,6)
  AND image_url LIKE '%/images/%';


-- (검증용) 필요하면 로컬에서만 잠깐 확인
-- SELECT store_id, sort_order, name, image FROM menu WHERE store_id IN (1,2,3,4,5,6) ORDER BY store_id, sort_order;
-- SELECT store_id, sort_order, image_url FROM store_image WHERE store_id IN (1,2,3,4,5,6) ORDER BY store_id, sort_order;