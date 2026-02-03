package com.posit.posit.domain.coupon.dto.response;

import com.posit.posit.domain.coupon.entity.IssuedCoupon;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class MyCouponListResponse {

    private List<CouponInfo> coupons;
    private Meta meta;

    @Getter
    @Builder
    public static class CouponInfo {
        private Long couponId;
        private String storeName;
        private String title;
        private LocalDateTime expiredAt; // 만료일
        private String imageUrl;

        public static CouponInfo from(IssuedCoupon coupon) {
            // 만료일 계산: 발급일 + 유효기간
            LocalDateTime expiredAt = coupon.getIssuedAt()
                    .plusDays(coupon.getTemplate().getValidDays());

            return CouponInfo.builder()
                    .couponId(coupon.getId())
                    .storeName(coupon.getTemplate().getCreatedBy().getStore().getName())
                    .title(coupon.getTemplate().getTitle())
                    .expiredAt(expiredAt)
                    .imageUrl(coupon.getTemplate().getImage())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class Meta {
        private Long nextCursor;
        private boolean hasNext;
    }
}