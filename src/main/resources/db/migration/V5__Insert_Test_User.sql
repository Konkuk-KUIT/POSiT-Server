-- V5__Insert_Test_User.sql
-- 설명: 테스트용 일반 유저(손님) 계정 생성

INSERT INTO users (
    role,
    login_id,
    password,
    name,
    phone,
    gender,
    birth,
    created_at,
    updated_at
) VALUES (
             'GUEST',
             'test_user',
             '$2a$10$8.UnVuG9HHgffUDAlk8qfOpFte2uTb.X.gij.w.s5x.x5x.x5x', -- password123
             '테스트손님',
             '01099999999',
             'MALE',
             '2000-01-01',
             NOW(),
             NOW()
         );