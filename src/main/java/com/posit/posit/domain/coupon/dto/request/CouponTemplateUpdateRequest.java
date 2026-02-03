package com.posit.posit.domain.coupon.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CouponTemplateUpdateRequest {

    @NotBlank(message = "쿠폰 제목은 필수입니다.")
    private String title;

    @NotBlank(message = "쿠폰 사용 조건은 필수입니다.")
    private String condition; // 예: "1만원 이상 구매 시"

    private String image; // 이미지 URL (없으면 null 가능)

    @NotNull(message = "유효 기간(일)은 필수입니다.")
    @Positive(message = "유효 기간은 0보다 커야 합니다.")
    private Integer validDays;
}