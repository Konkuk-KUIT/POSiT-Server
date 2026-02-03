package com.posit.posit.global.s3;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.Headers;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.posit.posit.domain.image.dto.response.PresignedUrlResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    // Pre-signed URL 생성 메서드
    public PresignedUrlResponse.PresignedUrlItem getPresignedUrl(String prefix, String fileName, String contentType) {

        // 1. 파일명 중복 방지 (UUID + 원본파일명)
        // 예: uploads/memos/uuid_image.jpg
        String imageKey = "uploads/" + prefix + "/" + UUID.randomUUID() + "_" + fileName;

        // 2. 유효 시간 설정 (3분 = 180초)
        Date expiration = new Date();
        long expTimeMillis = expiration.getTime();
        expTimeMillis += 1000 * 60 * 3;
        expiration.setTime(expTimeMillis);

        // 3. Pre-signed URL 요청 생성
        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(bucket, imageKey)
                        .withMethod(HttpMethod.PUT) // 프론트에서 PUT으로 업로드해야 함
                        .withExpiration(expiration);

        // 중요: 프론트에서 보낼 Content-Type과 일치해야 함 (보안 강화)
        generatePresignedUrlRequest.addRequestParameter(Headers.CONTENT_TYPE, contentType);

        // 4. URL 생성
        URL url = amazonS3.generatePresignedUrl(generatePresignedUrlRequest);

        return PresignedUrlResponse.PresignedUrlItem.builder()
                .uploadUrl(url.toString())
                .imageKey(imageKey) // 프론트는 이 키를 DB 저장용 API에 보내줘야 함
                .expiresInSeconds(180)
                .build();
    }

    // (기존 uploadImage 메서드는 놔두셔도 됩니다)
}