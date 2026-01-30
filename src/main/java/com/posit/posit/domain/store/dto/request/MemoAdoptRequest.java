package com.posit.posit.domain.store.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemoAdoptRequest {
    @NotNull
    private Long couponTemplateId; // 어떤 쿠폰을 줄 건지

    private String message;        // (선택) 사장님의 한마디
}