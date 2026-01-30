package com.posit.posit.domain.store.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CouponTemplateCreateRequest {

    // storeId 삭제됨 (불필요)

    @NotBlank
    private String title;       // "아메리카노 1잔 무료" (DB 제한: 20자)

    private String description; // "리뷰 감사 선물" (DB 제한: 50자)

    private String image;       // 이미지 URL

    @Min(1)
    private Integer validDays;  // 30 (일)
}