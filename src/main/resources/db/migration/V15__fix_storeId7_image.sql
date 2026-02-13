-- 더이퀄리브리엄커피 이미지 교체 (business_number = 1000000006)

START TRANSACTION;

SELECT id INTO @storeId
FROM store
WHERE business_number = '1000000006';

SELECT @storeId AS store_id;

-- 기존 이미지 삭제
DELETE FROM store_image
WHERE store_id = @storeId;

INSERT INTO store_image (store_id, image_url, thumbnail_url, sort_order)
VALUES
    (@storeId, 'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/uploads/stores/TheEqual1.jpeg', 'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/uploads/stores/TheEqual1.jpeg', 1),
    (@storeId, 'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/uploads/stores/TheEqual2.jpeg', 'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/uploads/stores/TheEqual2.jpeg', 2),
    (@storeId, 'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/uploads/stores/TheEqual3.jpeg', 'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/uploads/stores/TheEqual3.jpeg', 3),
    (@storeId, 'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/uploads/stores/TheEqual4.jpeg', 'https://posit-deploy.s3.ap-northeast-2.amazonaws.com/uploads/stores/TheEqual4.jpeg', 4);
