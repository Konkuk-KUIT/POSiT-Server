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
-- Store seed fix: open_time format ("HH:mm-HH:mm") and move options -> store_convince
-- -----------------------------------------------------------------------------

-- 1) Fix open_time to "HH:mm-HH:mm"
UPDATE store SET open_time = '10:00-24:00' WHERE id = 1;
UPDATE store SET open_time = '12:00-22:00' WHERE id = 2;
UPDATE store SET open_time = '11:00-22:30' WHERE id = 3;
UPDATE store SET open_time = '12:00-20:30' WHERE id = 4;
UPDATE store SET open_time = '12:00-22:00' WHERE id = 5;
UPDATE store SET open_time = '12:00-22:00' WHERE id = 6;

-- 2) Fix not_open
UPDATE store SET not_open = NULL WHERE id IN (1,2,3,4);
UPDATE store SET not_open = 'TUE' WHERE id IN (5,6);

-- 3) Seed TYPE filters (store type chips) and map stores via store_filter
-- NOTE: filter.code is VARCHAR(10), so keep codes short.
INSERT INTO filter (category, code, display_name) VALUES
  ('TYPE', 'STUDY',   '스터디 카페'),
  ('TYPE', 'BRUNCH',  '브런치 카페'),
  ('TYPE', 'DESSERT', '디저트 카페')
ON DUPLICATE KEY UPDATE
  display_name = VALUES(display_name);

-- Map store -> TYPE filter
INSERT IGNORE INTO store_filter (store_id, filter_id)
SELECT 1, f.id FROM filter f WHERE f.category='TYPE' AND f.code='DESSERT';

INSERT IGNORE INTO store_filter (store_id, filter_id)
SELECT 2, f.id FROM filter f WHERE f.category='TYPE' AND f.code='DESSERT';

INSERT IGNORE INTO store_filter (store_id, filter_id)
SELECT 3, f.id FROM filter f WHERE f.category='TYPE' AND f.code='BRUNCH';

INSERT IGNORE INTO store_filter (store_id, filter_id)
SELECT 4, f.id FROM filter f WHERE f.category='TYPE' AND f.code='DESSERT';

INSERT IGNORE INTO store_filter (store_id, filter_id)
SELECT 5, f.id FROM filter f WHERE f.category='TYPE' AND f.code='BRUNCH';
INSERT IGNORE INTO store_filter (store_id, filter_id)
SELECT 5, f.id FROM filter f WHERE f.category='TYPE' AND f.code='DESSERT';

INSERT IGNORE INTO store_filter (store_id, filter_id)
SELECT 6, f.id FROM filter f WHERE f.category='TYPE' AND f.code='DESSERT';

-- 4) Map conveniences into store_convince (options extracted from the Excel row)

-- Store 1: TAKEOUT, RESERVATION, WIFI, PET_FRIENDLY
INSERT IGNORE INTO store_convince (store_id, convince_id)
SELECT 1, c.id FROM convince c
WHERE c.code IN ('TAKEOUT','RESERVATION','WIFI','PET_FRIENDLY');

-- Store 2: WIFI, GROUP_SEAT, TAKEOUT, DELIVERY
INSERT IGNORE INTO store_convince (store_id, convince_id)
SELECT 2, c.id FROM convince c
WHERE c.code IN ('WIFI','GROUP_SEAT','TAKEOUT','DELIVERY');

-- Store 3: GROUP_SEAT, WIFI, TAKEOUT
INSERT IGNORE INTO store_convince (store_id, convince_id)
SELECT 3, c.id FROM convince c
WHERE c.code IN ('GROUP_SEAT','WIFI','TAKEOUT');

-- Store 4: TAKEOUT, RESERVATION, WIFI
INSERT IGNORE INTO store_convince (store_id, convince_id)
SELECT 4, c.id FROM convince c
WHERE c.code IN ('TAKEOUT','RESERVATION','WIFI');

-- Store 5: TAKEOUT, WIFI, DELIVERY, GROUP_SEAT
-- Excel source had a typo like "무와이파이"; we normalize to WIFI.
INSERT IGNORE INTO store_convince (store_id, convince_id)
SELECT 5, c.id FROM convince c
WHERE c.code IN ('TAKEOUT','WIFI','DELIVERY','GROUP_SEAT');

-- Store 6: WIFI, TAKEOUT
INSERT IGNORE INTO store_convince (store_id, convince_id)
SELECT 6, c.id FROM convince c
WHERE c.code IN ('WIFI','TAKEOUT');

-- Optional sanity checks
-- SELECT s.id, s.name, GROUP_CONCAT(c.code ORDER BY c.code SEPARATOR ',') AS convinces
-- FROM store s
-- LEFT JOIN store_convince sc ON sc.store_id = s.id
-- LEFT JOIN convince c ON c.id = sc.convince_id
-- WHERE s.id BETWEEN 1 AND 6
-- GROUP BY s.id, s.name;
