-- ============================================================
-- 설명: 사장님 계정 일괄 생성 (테스트 계정 1개 + 가게별 계정 6개)
-- ============================================================

-- 1. [공통] 테스트용 사장님 (test_owner) -> 사업자번호 10자리(1234567890)
INSERT INTO users (role, login_id, password, name, phone, gender, birth, created_at, updated_at)
VALUES ('OWNER', 'test_owner', '$2a$10$T0kcl6QQcMc5oDMdOYvIRecSCH179zPaLY57Y/uEhQoOy93U8qyRO', '테스트사장', '01012345678', 'MALE', '1990-01-01', NOW(), NOW());

INSERT INTO owner_profile (user_id, business_number)
SELECT id, '1234567890' FROM users WHERE login_id = 'test_owner';


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