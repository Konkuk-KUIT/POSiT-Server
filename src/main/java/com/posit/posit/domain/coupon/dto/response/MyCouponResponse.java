package com.posit.posit.domain.coupon.dto.response;

import com.posit.posit.domain.coupon.entity.IssuedCoupon;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class MyCouponResponse {
    private Long couponId;
    private Long storeId;
    private String storeName; // 가게 이름 추가
    private String title;
    private String condition;
    private LocalDateTime expiredAt;
    private String imageUrl;
    private String status;    // 상태(AVAILABLE, USED, EXPIRED) 추가

    public static MyCouponResponse from(IssuedCoupon coupon) {
        // 만료일 계산
        LocalDateTime expiredAt = coupon.getIssuedAt()
                .plusDays(coupon.getTemplate().getValidDays());

        // 가게 정보
        var store = coupon.getTemplate().getCreatedBy().getStore();

        return MyCouponResponse.builder()
                .couponId(coupon.getId())
                .storeId(store.getId())
                .storeName(store.getName())
                .title(coupon.getTemplate().getTitle())
                .condition(coupon.getTemplate().getDescription())
                .expiredAt(expiredAt)
                .imageUrl(coupon.getTemplate().getImage())
                .status(coupon.getStatus().name())
                .build();
    }
}