package com.posit.posit.domain.store.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CouponTemplateResponse {
    private Long templateId;
    private String title;
    private String description;
    private int validDays;
}