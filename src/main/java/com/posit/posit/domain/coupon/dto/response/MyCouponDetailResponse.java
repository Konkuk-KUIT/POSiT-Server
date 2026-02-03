package com.posit.posit.domain.coupon.dto.response;

import com.posit.posit.domain.coupon.entity.IssuedCoupon;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class MyCouponDetailResponse {

    private Long couponId;
    private Long storeId;
    private String title;
    private String condition; // 사용 조건 (Template의 description 활용)
    private LocalDateTime expiredAt;
    private String imageUrl;

    public static MyCouponDetailResponse from(IssuedCoupon coupon) {
        // 만료일 계산
        LocalDateTime expiredAt = coupon.getIssuedAt()
                .plusDays(coupon.getTemplate().getValidDays());

        // 가게 정보 가져오기 (User -> Store)
        Long storeId = coupon.getTemplate().getCreatedBy().getStore().getId();

        return MyCouponDetailResponse.builder()
                .couponId(coupon.getId())
                .storeId(storeId)
                .title(coupon.getTemplate().getTitle())
                // 'condition'은 보통 템플릿의 상세 설명(description)을 매핑합니다.
                .condition(coupon.getTemplate().getDescription())
                .expiredAt(expiredAt)
                .imageUrl(coupon.getTemplate().getImage())
                .build();
    }
}