package com.posit.posit.domain.store.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore; // 👈 이 import 필수!
import com.posit.posit.domain.coupon.entity.IssuedCoupon;
import com.posit.posit.domain.coupon.entity.IssuedCouponStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class CouponManagementResponse {

    private CouponSummary summary;
    private List<CouponItem> items;

    // 👇 JSON 응답(data)에서는 숨기고, Controller에서만 꺼내 쓸 수 있게 설정
    @JsonIgnore
    private Long nextCursorId;

    @JsonIgnore
    private boolean hasNext;

    // --- 1. 통계 DTO ---
    @Getter
    @Builder
    public static class CouponSummary {
        private long totalIssuedCount; // 누적 지급 수
        private long usedCount;        // 사용 완료
        private long unusedCount;      // 미사용
    }

    // --- 2. 개별 쿠폰 아이템 DTO ---
    @Getter
    @Builder
    public static class CouponItem {
        private Long issuedCouponId;
        private LocalDate issuedDate;
        private String userName;
        private String couponTitle;
        private String couponThumbnailUrl;
        private int quantity;
        private String status;

        public static CouponItem from(IssuedCoupon coupon) {
            return CouponItem.builder()
                    .issuedCouponId(coupon.getId())
                    .issuedDate(coupon.getIssuedAt().toLocalDate())
                    .userName(coupon.getUser().getName())
                    .couponTitle(coupon.getTemplate().getTitle())
                    .couponThumbnailUrl(coupon.getTemplate().getImage())
                    .quantity(1)
                    .status(coupon.getStatus().name())
                    .build();
        }
    }
}