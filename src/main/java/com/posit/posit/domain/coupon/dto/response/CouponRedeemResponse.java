package com.posit.posit.domain.coupon.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CouponRedeemResponse {
    private Long couponId;
    private String status;   // USED
    private String usedAt;   // 사용 시각 (ISO 8601)
    private Long storeId;
}