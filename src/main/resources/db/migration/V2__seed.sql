-- -------------------------------------------------
-- phone_verification : demo whitelist
-- -------------------------------------------------

INSERT INTO phone_verification (
    phone,
    code_hash,
    expired_at,
    verified_at,
    attempt_count,
    resend_count,
    status
) VALUES (
             '01012345678',                       -- 화이트리스트 번호
             '$2a$10$demodemocodedemodemoabcdef', -- 임의의 hash 문자열 (bcrypt 흉내)
             DATE_ADD(NOW(), INTERVAL 1 DAY),     -- 만료: 내일
             NOW(),                               -- 이미 인증된 상태로 처리
             0,
             0,
             'VERIFIED'
         );

-- -------------------------------------------------
-- store : demo data for OWNER signup
-- -------------------------------------------------

INSERT INTO store (
    owner_id,
    name,
    phone,
    description,
    category,
    open_time,
    not_open,
    latitude,
    longitude,
    road_address,
    lot_address,
    sns_link,
    coupon_pin_hash,
    business_number
) VALUES (
             NULL,                                  -- owner_id (회원가입 시 매핑 예정)
             '데모 카페',                           -- name
             '02-1234-5678',                        -- phone
             '사장님 회원가입 테스트용 매장입니다', -- description
             'CAFE',                                -- category
             '09:00-22:00',                         -- open_time
             'SUN',                                 -- not_open
             37.5665350,                            -- latitude
             126.9779692,                           -- longitude
             '서울특별시 중구 세종대로 110',         -- road_address
             '서울특별시 중구 태평로1가',            -- lot_address
             'https://instagram.com/demo_cafe',     -- sns_link
             NULL,                                  -- coupon_pin_hash
             '1234567890'                           -- business_number
         );