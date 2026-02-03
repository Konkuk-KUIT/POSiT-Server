package com.posit.posit.domain.store.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ConcernUpdateRequest {

    @NotBlank(message = "수정할 고민 내용을 입력해주세요.")
    private String concernContent;

    @NotNull(message = "템플릿 ID는 필수입니다.")
    private Long templateId;
}