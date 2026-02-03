package com.posit.posit.domain.coupon.dto.response;

import com.posit.posit.domain.coupon.entity.CouponTemplate;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CouponTemplateUpdateResponse {

    private Long templateId;
    private String title;
    private int validDays;

    public static CouponTemplateUpdateResponse from(CouponTemplate template) {
        return CouponTemplateUpdateResponse.builder()
                .templateId(template.getId())
                .title(template.getTitle())
                .validDays(template.getValidDays())
                .build();
    }
}