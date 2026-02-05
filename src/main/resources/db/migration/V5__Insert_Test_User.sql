-- V5__Insert_Test_User.sql
-- 설명: 테스트용 일반 유저(손님) 계정 생성

INSERT INTO users (
    role,
    login_id,
    password,
    name,
    phone_number,
    gender,
    birth,
    created_at,
    modified_at
) VALUES (
             'USER',                                                                   -- 역할: 일반 유저
             'test_user',                                                              -- 아이디
             '$2a$10$8.UnVuG9HHgffUDAlk8qfOpFte2uTb.X.gij.w.s5x.x5x.x5x',              -- 비밀번호: password123
             '테스트손님',
             '010-9999-9999',
             'MALE',
             '2000-01-01',
             NOW(),
             NOW()
         );