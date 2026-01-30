package com.posit.posit.domain.store.dto.request;

import com.posit.posit.domain.memo.entity.RejectCode;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemoRejectRequest {
    @NotNull
    private RejectCode rejectCode; // 거절 사유 코드 (ENUM)

    private String message;        // (선택) 구체적인 거절 사유 텍스트
}