package com.posit.posit.domain.image.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PresignedUrlResponse {

    private List<PresignedUrlItem> items;

    @Getter
    @Builder
    public static class PresignedUrlItem {
        private String uploadUrl;       // S3에 직접 쏠 주소 (PUT)
        private String imageKey;        // 나중에 DB에 저장할 키 (UUID 포함됨)
        private long expiresInSeconds;  // 유효 시간
    }
}