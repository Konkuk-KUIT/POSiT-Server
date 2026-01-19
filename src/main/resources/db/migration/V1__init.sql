-- -------------------------------------------------
-- V1__init.sql
-- -------------------------------------------------
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- 1) users
CREATE TABLE users (
                       id BIGINT NOT NULL AUTO_INCREMENT,
                       login_id VARCHAR(15) NOT NULL,
                       password VARCHAR(255) NOT NULL COMMENT 'hash값으로 저장',
                       name VARCHAR(10) NOT NULL,
                       phone VARCHAR(11) NOT NULL COMMENT 'unique',
                       role ENUM('OWNER','GUEST') NOT NULL,
                       status ENUM('ACTIVE','INACTIVE') NOT NULL DEFAULT 'ACTIVE',
                       created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                       last_login_at DATETIME NULL,
                       PRIMARY KEY (id),
                       UNIQUE KEY uq_users_login_id (login_id),
                       UNIQUE KEY uq_users_phone (phone)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2) owner_profile (status 제거)
CREATE TABLE owner_profile (
                               id BIGINT NOT NULL AUTO_INCREMENT,
                               business_number VARCHAR(10) NOT NULL COMMENT '- 포함 안한 숫자만 10자리',
                               user_id BIGINT NOT NULL,
                               PRIMARY KEY (id),
                               UNIQUE KEY uq_owner_profile_user (user_id),
                               UNIQUE KEY uq_owner_profile_business_number (business_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3) store
CREATE TABLE store (
                       id BIGINT NOT NULL AUTO_INCREMENT,
                       owner_id BIGINT NOT NULL,
                       name VARCHAR(20) NOT NULL,
                       description VARCHAR(50) NOT NULL,
                       category ENUM('CAFE','RESTAURANT') NOT NULL DEFAULT 'CAFE',
                       open_time VARCHAR(20) NOT NULL COMMENT '"HH:mm-HH:mm"',
                       not_open ENUM('MON','TUE','WED','THU','FRI','SAT','SUN') NULL,
                       latitude DECIMAL(10,7) NOT NULL,
                       longitude DECIMAL(10,7) NOT NULL,
                       road_address VARCHAR(30) NOT NULL,
                       lot_address VARCHAR(30) NULL,
                       sns_link VARCHAR(255) NULL,
                       coupon_pin_hash VARCHAR(255) NOT NULL,
                       PRIMARY KEY (id),
                       KEY idx_store_owner (owner_id),
                       KEY idx_store_geo (latitude, longitude)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4) store_image
CREATE TABLE store_image (
                             id BIGINT NOT NULL AUTO_INCREMENT,
                             store_id BIGINT NOT NULL,
                             image_url VARCHAR(2048) NOT NULL,
                             thumbnail_url VARCHAR(2048) NOT NULL,
                             sort_order INT NOT NULL,
                             created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                             PRIMARY KEY (id),
                             KEY idx_store_image_store (store_id),
                             KEY idx_store_image_store_order (store_id, sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 5) menu
CREATE TABLE menu (
                      id BIGINT NOT NULL AUTO_INCREMENT,
                      store_id BIGINT NOT NULL,
                      name VARCHAR(20) NOT NULL,
                      price INT NOT NULL,
                      sort_order INT NULL,
                      created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                      updated_at DATETIME NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                      image VARCHAR(2048) NULL,
                      PRIMARY KEY (id),
                      KEY idx_menu_store (store_id),
                      KEY idx_menu_store_order (store_id, sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 6) coupon_template
CREATE TABLE coupon_template (
                                 id BIGINT NOT NULL AUTO_INCREMENT,
                                 title VARCHAR(20) NOT NULL,
                                 description VARCHAR(50) NULL,
                                 image TEXT NULL COMMENT 'image URL',
                                 valid_days INT NOT NULL COMMENT '발급 시간으로부터 며칠 유지될지',
                                 created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                 updated_at DATETIME NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                 created_by_user_id BIGINT NOT NULL,
                                 PRIMARY KEY (id),
                                 KEY idx_coupon_template_creator (created_by_user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 7) concern
CREATE TABLE concern (
                         id BIGINT NOT NULL AUTO_INCREMENT,
                         content VARCHAR(1000) NULL,
                         status ENUM('OPEN','CLOSED') NOT NULL DEFAULT 'OPEN',
                         created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         updated_at DATETIME NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                         store_id BIGINT NOT NULL,
                         template_id BIGINT NOT NULL,
                         PRIMARY KEY (id),
                         KEY idx_concern_store (store_id),
                         KEY idx_concern_template (template_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 8) memo
CREATE TABLE memo (
                      id BIGINT NOT NULL AUTO_INCREMENT,
                      memo_type ENUM('ANSWER','FREE') NOT NULL,
                      free_type ENUM('TIP','MARKETING','MENU_DEV','TREND','CUSTOMER_SERVICE') NULL,
                      title VARCHAR(20) NOT NULL,
                      content VARCHAR(150) NOT NULL,
                      image TEXT NULL,
                      status ENUM('REVIEWING','ADOPTED','REJECTED') NOT NULL DEFAULT 'REVIEWING',
                      created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                      updated_at DATETIME NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                      store_id BIGINT NOT NULL,
                      user_id BIGINT NOT NULL,
                      concern_id BIGINT NULL,
                      PRIMARY KEY (id),
                      KEY idx_memo_store (store_id),
                      KEY idx_memo_user (user_id),
                      KEY idx_memo_concern (concern_id),
                      KEY idx_memo_store_created (store_id, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 9) decision (memo:decision = 1:1 이라면 UNIQUE(memo_id) 걸어도 됨)
CREATE TABLE decision (
                          id BIGINT NOT NULL AUTO_INCREMENT,
                          memo_id BIGINT NOT NULL,
                          type ENUM('ADOPT','REJECT') NOT NULL,
                          message VARCHAR(50) NULL,
                          created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          coupon_template_id BIGINT NULL COMMENT '채택시에만',
                          reject_code ENUM('BUDGET','REALISTIC','ALREADY','ECT') NULL,
                          PRIMARY KEY (id),
                          UNIQUE KEY uq_decision_memo (memo_id),
                          KEY idx_decision_coupon_template (coupon_template_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 10) issued_coupon
CREATE TABLE issued_coupon (
                               id BIGINT NOT NULL AUTO_INCREMENT,
                               store_id BIGINT NOT NULL,
                               memo_id BIGINT NOT NULL,
                               template_id BIGINT NOT NULL,
                               user_id BIGINT NOT NULL,
                               title VARCHAR(20) NOT NULL,
                               description VARCHAR(1000) NULL,
                               image TEXT NULL,
                               `condition` VARCHAR(30) NOT NULL,
                               issued_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                               expired_at DATETIME NOT NULL,
                               used_at DATETIME NULL,
                               status ENUM('ISSUED','USED','EXPIRED') NOT NULL,
                               PRIMARY KEY (id),
                               KEY idx_issued_coupon_user (user_id),
                               KEY idx_issued_coupon_store (store_id),
                               KEY idx_issued_coupon_status_exp (status, expired_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 11) auth_refresh_token
CREATE TABLE auth_refresh_token (
                                    id BIGINT NOT NULL AUTO_INCREMENT,
                                    token_hash VARCHAR(255) NOT NULL COMMENT '토큰의 해시값 저장',
                                    expired_at DATETIME NOT NULL,
                                    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                    revoked_at DATETIME NULL,
                                    user_id BIGINT NOT NULL,
                                    PRIMARY KEY (id),
                                    KEY idx_refresh_token_user (user_id),
                                    KEY idx_refresh_token_expired (expired_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 12) phone_verification
CREATE TABLE phone_verification (
                                    id BIGINT NOT NULL AUTO_INCREMENT,
                                    phone VARCHAR(11) NOT NULL,
                                    code_hash VARCHAR(200) NOT NULL,
                                    expired_at DATETIME NOT NULL,
                                    verified_at DATETIME NULL,
                                    attempt_count INT NOT NULL DEFAULT 0,
                                    resend_count INT NOT NULL DEFAULT 0,
                                    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                    status ENUM('PENDING','VERIFIED','EXPIRED','LOCKED','CANCELLED') NOT NULL DEFAULT 'PENDING',
                                    PRIMARY KEY (id),
                                    KEY idx_phone_created (phone, created_at),
                                    KEY idx_phone_expired (phone, expired_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 13) filter
CREATE TABLE filter (
                        id BIGINT NOT NULL AUTO_INCREMENT,
                        category VARCHAR(20) NOT NULL COMMENT 'TYPE, MOOD',
                        code VARCHAR(10) NOT NULL,
                        display_name VARCHAR(20) NOT NULL,
                        created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        PRIMARY KEY (id),
                        UNIQUE KEY uq_filter_code (code),
                        UNIQUE KEY uq_filter_display_name (display_name),
                        KEY idx_filter_category (category)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 14) store_filter (JPA-friendly)
CREATE TABLE store_filter (
                              id BIGINT NOT NULL AUTO_INCREMENT,
                              store_id BIGINT NOT NULL,
                              filter_id BIGINT NOT NULL,
                              PRIMARY KEY (id),
                              UNIQUE KEY uq_store_filter (store_id, filter_id),
                              KEY idx_store_filter_store (store_id),
                              KEY idx_store_filter_filter (filter_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 15) convince
CREATE TABLE convince (
                          id BIGINT NOT NULL AUTO_INCREMENT,
                          display_name VARCHAR(20) NOT NULL,
                          code VARCHAR(20) NOT NULL,
                          PRIMARY KEY (id),
                          UNIQUE KEY uq_convince_code (code),
                          UNIQUE KEY uq_convince_display (display_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 16) store_convince (JPA-friendly)
CREATE TABLE store_convince (
                                id BIGINT NOT NULL AUTO_INCREMENT,
                                store_id BIGINT NOT NULL,
                                convince_id BIGINT NOT NULL,
                                PRIMARY KEY (id),
                                UNIQUE KEY uq_store_convince (store_id, convince_id),
                                KEY idx_store_convince_store (store_id),
                                KEY idx_store_convince_convince (convince_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 17) review
CREATE TABLE review (
                        id BIGINT NOT NULL AUTO_INCREMENT,
                        created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        store_id BIGINT NOT NULL,
                        user_id BIGINT NOT NULL,
                        PRIMARY KEY (id),
                        KEY idx_review_store (store_id),
                        KEY idx_review_user (user_id),
                        KEY idx_review_store_created (store_id, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 18) review_item
CREATE TABLE review_item (
                             id BIGINT NOT NULL AUTO_INCREMENT,
                             code VARCHAR(20) NOT NULL,
                             display_name VARCHAR(255) NOT NULL,
                             PRIMARY KEY (id),
                             UNIQUE KEY uq_review_item_code (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 19) review_choice (review - review_item N:M)
CREATE TABLE review_choice (
                               id BIGINT NOT NULL AUTO_INCREMENT,
                               review_id BIGINT NOT NULL,
                               review_item_id BIGINT NOT NULL,
                               created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                               PRIMARY KEY (id),
                               UNIQUE KEY uq_review_choice (review_id, review_item_id),
                               KEY idx_review_choice_review (review_id),
                               KEY idx_review_choice_item (review_item_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -------------------------------------------------
-- FK (ALL RESTRICT)
-- -------------------------------------------------

ALTER TABLE owner_profile
    ADD CONSTRAINT fk_owner_profile_user
        FOREIGN KEY (user_id) REFERENCES users(id)
            ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE store
    ADD CONSTRAINT fk_store_owner
        FOREIGN KEY (owner_id) REFERENCES users(id)
            ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE store_image
    ADD CONSTRAINT fk_store_image_store
        FOREIGN KEY (store_id) REFERENCES store(id)
            ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE menu
    ADD CONSTRAINT fk_menu_store
        FOREIGN KEY (store_id) REFERENCES store(id)
            ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE coupon_template
    ADD CONSTRAINT fk_coupon_template_creator
        FOREIGN KEY (created_by_user_id) REFERENCES users(id)
            ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE concern
    ADD CONSTRAINT fk_concern_store
        FOREIGN KEY (store_id) REFERENCES store(id)
            ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE concern
    ADD CONSTRAINT fk_concern_template
        FOREIGN KEY (template_id) REFERENCES coupon_template(id)
            ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE memo
    ADD CONSTRAINT fk_memo_store
        FOREIGN KEY (store_id) REFERENCES store(id)
            ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE memo
    ADD CONSTRAINT fk_memo_user
        FOREIGN KEY (user_id) REFERENCES users(id)
            ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE memo
    ADD CONSTRAINT fk_memo_concern
        FOREIGN KEY (concern_id) REFERENCES concern(id)
            ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE decision
    ADD CONSTRAINT fk_decision_memo
        FOREIGN KEY (memo_id) REFERENCES memo(id)
            ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE decision
    ADD CONSTRAINT fk_decision_coupon_template
        FOREIGN KEY (coupon_template_id) REFERENCES coupon_template(id)
            ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE issued_coupon
    ADD CONSTRAINT fk_issued_coupon_store
        FOREIGN KEY (store_id) REFERENCES store(id)
            ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE issued_coupon
    ADD CONSTRAINT fk_issued_coupon_memo
        FOREIGN KEY (memo_id) REFERENCES memo(id)
            ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE issued_coupon
    ADD CONSTRAINT fk_issued_coupon_template
        FOREIGN KEY (template_id) REFERENCES coupon_template(id)
            ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE issued_coupon
    ADD CONSTRAINT fk_issued_coupon_user
        FOREIGN KEY (user_id) REFERENCES users(id)
            ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE auth_refresh_token
    ADD CONSTRAINT fk_refresh_token_user
        FOREIGN KEY (user_id) REFERENCES users(id)
            ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE store_filter
    ADD CONSTRAINT fk_store_filter_store
        FOREIGN KEY (store_id) REFERENCES store(id)
            ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE store_filter
    ADD CONSTRAINT fk_store_filter_filter
        FOREIGN KEY (filter_id) REFERENCES filter(id)
            ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE store_convince
    ADD CONSTRAINT fk_store_convince_store
        FOREIGN KEY (store_id) REFERENCES store(id)
            ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE store_convince
    ADD CONSTRAINT fk_store_convince_convince
        FOREIGN KEY (convince_id) REFERENCES convince(id)
            ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE review
    ADD CONSTRAINT fk_review_store
        FOREIGN KEY (store_id) REFERENCES store(id)
            ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE review
    ADD CONSTRAINT fk_review_user
        FOREIGN KEY (user_id) REFERENCES users(id)
            ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE review_choice
    ADD CONSTRAINT fk_review_choice_review
        FOREIGN KEY (review_id) REFERENCES review(id)
            ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE review_choice
    ADD CONSTRAINT fk_review_choice_item
        FOREIGN KEY (review_item_id) REFERENCES review_item(id)
            ON DELETE RESTRICT ON UPDATE RESTRICT;

SET FOREIGN_KEY_CHECKS = 1;