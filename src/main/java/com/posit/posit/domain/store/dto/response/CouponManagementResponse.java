package com.posit.posit.domain.store.dto.response;

import com.posit.posit.domain.coupon.entity.IssuedCoupon; // 발급된 쿠폰 엔티티 (가정)
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
    private Long nextCursorId;
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
        private int quantity; // 보통 1개
        private String status; // USED, UNUSED

        public static CouponItem from(IssuedCoupon coupon) {
            return CouponItem.builder()
                    .issuedCouponId(coupon.getId())
                    .issuedDate(coupon.getIssuedAt().toLocalDate())
                    .userName(coupon.getUser().getName())
                    .couponTitle(coupon.getTemplate().getTitle())
                    .couponThumbnailUrl(coupon.getTemplate().getImage())
                    .quantity(1) // 기본 1개로 설정 (수량 컬럼이 있다면 그것 사용)
                    .status(coupon.getStatus().name())
                    .build();
        }
    }
}