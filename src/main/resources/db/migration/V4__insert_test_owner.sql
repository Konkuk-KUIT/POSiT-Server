-- V4__insert_test_owner.sql

-- 1. 테스트용 사장님 계정 추가 (비밀번호: password123)
INSERT INTO users (
    login_id,
    password,
    name,
    phone_number,
    role,
    gender,
    birth,
    created_at,
    modified_at
) VALUES (
             'test_owner',
             '$2a$10$8.UnVuG9HHgffUDAlk8qfOpFte2uTb.X.gij.w.s5x.x5x.x5x', -- password123
             '테스트사장님',
             '010-1234-5678',
             'OWNER',
             'MALE',
             '1990-01-01',
             NOW(),
             NOW()
         );

-- 2. 사장님 프로필 연결 (위에서 만든 유저 ID를 찾아서 연결)
INSERT INTO owner_profile (user_id, business_number, created_at, modified_at)
SELECT id, '123-45-67890', NOW(), NOW()
FROM users
WHERE login_id = 'test_owner';

-- 설명: V3에서 등록한 6개 가게에 대한 사장님 계정 생성 및 연결

-- ==========================================
-- 1. 카페 레이지아워 (Business No: 1000000001) -> owner1
-- ==========================================
INSERT INTO users (role, login_id, password, name, phone_number, gender, birth, created_at, modified_at)
VALUES ('OWNER', 'owner1', '$2a$10$8.UnVuG9HHgffUDAlk8qfOpFte2uTb.X.gij.w.s5x.x5x.x5x', '레이지아워사장님', '010-1111-1111', 'FEMALE', '1995-01-01', NOW(), NOW());

INSERT INTO owner_profile (user_id, business_number, created_at, modified_at)
SELECT id, '1000000001', NOW(), NOW() FROM users WHERE login_id = 'owner1';

UPDATE store SET owner_id = (SELECT id FROM users WHERE login_id = 'owner1') WHERE business_number = '1000000001';


-- ==========================================
-- 2. 마이 디어 버터하우스 (Business No: 1000000002) -> owner2
-- ==========================================
INSERT INTO users (role, login_id, password, name, phone_number, gender, birth, created_at, modified_at)
VALUES ('OWNER', 'owner2', '$2a$10$8.UnVuG9HHgffUDAlk8qfOpFte2uTb.X.gij.w.s5x.x5x.x5x', '버터하우스사장님', '010-2222-2222', 'FEMALE', '1993-05-05', NOW(), NOW());

INSERT INTO owner_profile (user_id, business_number, created_at, modified_at)
SELECT id, '1000000002', NOW(), NOW() FROM users WHERE login_id = 'owner2';

UPDATE store SET owner_id = (SELECT id FROM users WHERE login_id = 'owner2') WHERE business_number = '1000000002';


-- ==========================================
-- 3. 도우터 (Business No: 1000000003) -> owner3
-- ==========================================
INSERT INTO users (role, login_id, password, name, phone_number, gender, birth, created_at, modified_at)
VALUES ('OWNER', 'owner3', '$2a$10$8.UnVuG9HHgffUDAlk8qfOpFte2uTb.X.gij.w.s5x.x5x.x5x', '도우터사장님', '010-3333-3333', 'MALE', '1988-08-08', NOW(), NOW());

INSERT INTO owner_profile (user_id, business_number, created_at, modified_at)
SELECT id, '1000000003', NOW(), NOW() FROM users WHERE login_id = 'owner3';

UPDATE store SET owner_id = (SELECT id FROM users WHERE login_id = 'owner3') WHERE business_number = '1000000003';


-- ==========================================
-- 4. café 462 (Business No: 1000000004) -> owner4
-- ==========================================
INSERT INTO users (role, login_id, password, name, phone_number, gender, birth, created_at, modified_at)
VALUES ('OWNER', 'owner4', '$2a$10$8.UnVuG9HHgffUDAlk8qfOpFte2uTb.X.gij.w.s5x.x5x.x5x', '462사장님', '010-4444-4444', 'FEMALE', '1990-12-25', NOW(), NOW());

INSERT INTO owner_profile (user_id, business_number, created_at, modified_at)
SELECT id, '1000000004', NOW(), NOW() FROM users WHERE login_id = 'owner4';

UPDATE store SET owner_id = (SELECT id FROM users WHERE login_id = 'owner4') WHERE business_number = '1000000004';


-- ==========================================
-- 5. 카페 언필드 (Business No: 1000000005) -> owner5
-- ==========================================
INSERT INTO users (role, login_id, password, name, phone_number, gender, birth, created_at, modified_at)
VALUES ('OWNER', 'owner5', '$2a$10$8.UnVuG9HHgffUDAlk8qfOpFte2uTb.X.gij.w.s5x.x5x.x5x', '언필드사장님', '010-5555-5555', 'MALE', '1996-06-06', NOW(), NOW());

INSERT INTO owner_profile (user_id, business_number, created_at, modified_at)
SELECT id, '1000000005', NOW(), NOW() FROM users WHERE login_id = 'owner5';

UPDATE store SET owner_id = (SELECT id FROM users WHERE login_id = 'owner5') WHERE business_number = '1000000005';


-- ==========================================
-- 6. 더이퀄리브리엄커피 (Business No: 1000000006) -> owner6
-- ==========================================
INSERT INTO users (role, login_id, password, name, phone_number, gender, birth, created_at, modified_at)
VALUES ('OWNER', 'owner6', '$2a$10$8.UnVuG9HHgffUDAlk8qfOpFte2uTb.X.gij.w.s5x.x5x.x5x', '이퀄리사장님', '010-6666-6666', 'MALE', '1992-02-02', NOW(), NOW());

INSERT INTO owner_profile (user_id, business_number, created_at, modified_at)
SELECT id, '1000000006', NOW(), NOW() FROM users WHERE login_id = 'owner6';

UPDATE store SET owner_id = (SELECT id FROM users WHERE login_id = 'owner6') WHERE business_number = '1000000006';