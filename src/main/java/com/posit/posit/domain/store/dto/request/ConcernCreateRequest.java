package com.posit.posit.domain.store.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ConcernCreateRequest {

    @NotBlank(message = "고민 내용은 필수입니다.")
    @Size(max = 1000)
    private String content;

    private Long templateId;
}