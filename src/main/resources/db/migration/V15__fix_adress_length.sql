-- 도로명 주소 길이 늘리기 (30 -> 255)
ALTER TABLE store MODIFY road_address VARCHAR(255) NOT NULL;

-- 지번 주소 길이 늘리기 (30 -> 255)
ALTER TABLE store MODIFY lot_address VARCHAR(255) NULL;