package com.posit.posit.domain.memo.dto.response;

import com.posit.posit.domain.memo.entity.Memo;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class MemoCreateResponse {

    private Long memoId;
    private String memoType;
    private LocalDateTime createdAt;
    private String status;

    public static MemoCreateResponse from(Memo memo) {
        return MemoCreateResponse.builder()
                .memoId(memo.getId())
                .memoType(memo.getMemoType().name())
                .createdAt(memo.getCreatedAt())
                .status(memo.getStatus().name()) // REVIEWING
                .build();
    }
}