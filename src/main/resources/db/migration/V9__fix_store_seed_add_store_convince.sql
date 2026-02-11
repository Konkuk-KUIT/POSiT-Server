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
  ('야외 좌석', 'OUTDOOR_SEAT')
ON DUPLICATE KEY UPDATE
  display_name = VALUES(display_name);


-- -----------------------------------------------------------------------------
-- Store seed fix :
--  - open_time format ("HH:mm-HH:mm")
--  - not_open
--  - TYPE filter mapping via store_filter
--  - conveniences mapping via store_convince
-- -----------------------------------------------------------------------------

-- Resolve store ids by business_number
SET @s1 := (SELECT id FROM store WHERE business_number = '1000000001');
SET @s2 := (SELECT id FROM store WHERE business_number = '1000000002');
SET @s3 := (SELECT id FROM store WHERE business_number = '1000000003');
SET @s4 := (SELECT id FROM store WHERE business_number = '1000000004');
SET @s5 := (SELECT id FROM store WHERE business_number = '1000000005');
SET @s6 := (SELECT id FROM store WHERE business_number = '1000000006');

-- 1) Fix open_time to "HH:mm-HH:mm"
UPDATE store SET open_time = '10:00-24:00' WHERE business_number = '1000000001';
UPDATE store SET open_time = '12:00-22:00' WHERE business_number = '1000000002';
UPDATE store SET open_time = '11:00-22:30' WHERE business_number = '1000000003';
UPDATE store SET open_time = '12:00-20:30' WHERE business_number = '1000000004';
UPDATE store SET open_time = '12:00-22:00' WHERE business_number = '1000000005';
UPDATE store SET open_time = '12:00-22:00' WHERE business_number = '1000000006';

-- 2) Fix not_open
UPDATE store SET not_open = NULL WHERE business_number IN ('1000000001','1000000002','1000000003','1000000004');
UPDATE store SET not_open = 'TUE' WHERE business_number IN ('1000000005','1000000006');

-- 3) Seed TYPE filters (store type chips) and map stores via store_filter
INSERT INTO filter (category, code, display_name) VALUES
  ('TYPE', 'STUDY',   '스터디 카페'),
  ('TYPE', 'BRUNCH',  '브런치 카페'),
  ('TYPE', 'DESSERT', '디저트 카페')
ON DUPLICATE KEY UPDATE
  display_name = VALUES(display_name);

-- Map store -> TYPE filter
INSERT IGNORE INTO store_filter (store_id, filter_id)
SELECT @s1, f.id FROM filter f WHERE f.category='TYPE' AND f.code='DESSERT';

INSERT IGNORE INTO store_filter (store_id, filter_id)
SELECT @s2, f.id FROM filter f WHERE f.category='TYPE' AND f.code='DESSERT';

INSERT IGNORE INTO store_filter (store_id, filter_id)
SELECT @s3, f.id FROM filter f WHERE f.category='TYPE' AND f.code='BRUNCH';

INSERT IGNORE INTO store_filter (store_id, filter_id)
SELECT @s4, f.id FROM filter f WHERE f.category='TYPE' AND f.code='DESSERT';

INSERT IGNORE INTO store_filter (store_id, filter_id)
SELECT @s5, f.id FROM filter f WHERE f.category='TYPE' AND f.code='BRUNCH';
INSERT IGNORE INTO store_filter (store_id, filter_id)
SELECT @s5, f.id FROM filter f WHERE f.category='TYPE' AND f.code='DESSERT';

INSERT IGNORE INTO store_filter (store_id, filter_id)
SELECT @s6, f.id FROM filter f WHERE f.category='TYPE' AND f.code='DESSERT';

-- 4) Map conveniences into store_convince (options extracted from the Excel row)

-- Store 1: TAKEOUT, RESERVATION, WIFI, PET_FRIENDLY
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

-- Optional sanity checks
-- SELECT business_number, id, name, open_time, not_open FROM store WHERE business_number IN ('1000000001','1000000002','1000000003','1000000004','1000000005','1000000006');
-- SELECT s.business_number, s.id, s.name, GROUP_CONCAT(DISTINCT f.code ORDER BY f.code SEPARATOR ',') AS type_filters
-- FROM store s
-- LEFT JOIN store_filter sf ON sf.store_id = s.id
-- LEFT JOIN filter f ON f.id = sf.filter_id AND f.category='TYPE'
-- WHERE s.business_number IN ('1000000001','1000000002','1000000003','1000000004','1000000005','1000000006')
-- GROUP BY s.business_number, s.id, s.name;
--
-- SELECT s.business_number, s.id, s.name, GROUP_CONCAT(DISTINCT c.code ORDER BY c.code SEPARATOR ',') AS convinces
-- FROM store s
-- LEFT JOIN store_convince sc ON sc.store_id = s.id
-- LEFT JOIN convince c ON c.id = sc.convince_id
-- WHERE s.business_number IN ('1000000001','1000000002','1000000003','1000000004','1000000005','1000000006')
-- GROUP BY s.business_number, s.id, s.name;
