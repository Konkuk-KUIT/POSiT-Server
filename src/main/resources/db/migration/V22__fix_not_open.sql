-- 기존 not_open 컬럼의 길이를 넉넉하게 늘려줍니다.
ALTER TABLE store MODIFY COLUMN not_open VARCHAR(255) NULL;