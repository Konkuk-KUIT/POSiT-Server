-- 기존에 NOT NULL로 걸려있던 제약조건을 NULL 허용으로 풀어주는 쿼리
ALTER TABLE concern MODIFY COLUMN template_id BIGINT NULL;