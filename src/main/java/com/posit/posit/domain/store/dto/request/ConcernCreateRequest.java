package com.posit.posit.domain.store.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ConcernCreateRequest {

    @NotBlank(message = "고민 내용은 필수입니다.")
    private String content; // 예: "매장 음악 추천해주세요!"

    @NotNull(message = "쿠폰 템플릿을 선택해야 합니다.")
    private Long templateId; // Step 1에서 만든 쿠폰 템플릿의 ID (예: 1번)
}