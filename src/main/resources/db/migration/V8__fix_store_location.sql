-- -----------------------------------------------------------------------------
-- V8__fix_store_location.sql
-- 설명: 테스트 데이터 보정
--  1) store 테이블의 도로명주소 + 위/경도(좌표) 업데이트
--  2) 테스트 계정 비밀번호 해시값 통일(password123)
-- -----------------------------------------------------------------------------
ALTER TABLE store
    MODIFY name        VARCHAR(30)  NOT NULL,
    MODIFY description VARCHAR(255) NOT NULL,
    MODIFY road_address VARCHAR(255) NOT NULL,
    MODIFY lot_address  VARCHAR(255) NULL;

-- 1) 가게 위치(주소/좌표) 보정
-- ※ 좌표 표기: (longitude, latitude)로 수집된 값을 DB 컬럼 (latitude, longitude)에 맞춰 저장

-- 1. 카페 레이지아워
UPDATE store
SET road_address = '서울특별시 광진구 아차산로33길 68 지하1층 레이지아워',
    latitude     = 37.543142,
    longitude    = 127.071267
WHERE business_number = '1000000001';

-- 2. 마이 디어 버터하우스
UPDATE store
SET road_address = '서울 광진구 능동로13길 74 1층',
    latitude     = 37.544937,
    longitude    = 127.069129
WHERE business_number = '1000000002';

-- 3. 도우터
UPDATE store
SET road_address = '서울특별시 광진구 아차산로31길 40 1층',
    latitude     = 37.542688,
    longitude    = 127.070172
WHERE business_number = '1000000003';

-- 4. café 462
UPDATE store
SET road_address = '서울 광진구 동일로24길 54 1층',
    latitude     = 37.543130,
    longitude    = 127.067928
WHERE business_number = '1000000004';

-- 5. 카페 언필드
UPDATE store
SET road_address = '서울 광진구 동일로22길 30 2층',
    latitude     = 37.542059,
    longitude    = 127.065919
WHERE business_number = '1000000005';

-- 6. 더이퀄리브리엄커피
UPDATE store
SET road_address = '서울 광진구 아차산로30길 7 3층 더이퀄리브리엄커피',
    latitude     = 37.540354,
    longitude    = 127.067831
WHERE business_number = '1000000006';


-- 2) 테스트 유저 비밀번호 해시 통일 (평문: password123)
UPDATE users
SET password = '$2a$10$T0kcl6QQcMc5oDMdOYvIRecSCH179zPaLY57Y/uEhQoOy93U8qyRO'
WHERE login_id IN (
    'test_owner',
    'owner1',
    'owner2',
    'owner3',
    'owner4',
    'owner5',
    'owner6'
);