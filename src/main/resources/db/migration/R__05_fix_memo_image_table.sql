
-- 2. [데이터 이관] 기존 memo 테이블에 이미지가 있던 경우, 새 테이블로 옮기기
-- (콤마로 구분된 데이터가 아니라 URL 하나만 들어있던 경우를 가정)
INSERT INTO memo_image (memo_id, image_url, created_at)
SELECT id, image, created_at
FROM memo
WHERE image IS NOT NULL AND image != '';

-- 3. 이제 안전하게 기존 컬럼 삭제
ALTER TABLE memo DROP COLUMN image;

-- 4. 외래키 제약조건 추가 (테이블 생성 후 데이터 넣고 마지막에 거는 게 성능상 좋음)
ALTER TABLE memo_image
    ADD CONSTRAINT fk_memo_image_memo
        FOREIGN KEY (memo_id) REFERENCES memo(id)
            ON DELETE CASCADE ON UPDATE RESTRICT;