package com.posit.posit.domain.coupon.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record CouponRedeemRequest(
        @Schema(description = "가게에서 제공하는 쿠폰 사용 비밀번호", example = "1234")
        @NotBlank(message = "비밀번호를 입력해주세요.")
        String couponPin
) {}