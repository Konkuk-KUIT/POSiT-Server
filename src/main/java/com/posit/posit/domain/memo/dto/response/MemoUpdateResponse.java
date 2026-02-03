package com.posit.posit.domain.memo.dto.response;

import com.posit.posit.domain.memo.entity.Memo;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class MemoUpdateResponse {

    private Long memoId;
    private LocalDateTime updatedAt;
    private String status;

    public static MemoUpdateResponse from(Memo memo) {
        return MemoUpdateResponse.builder()
                .memoId(memo.getId())
                .updatedAt(memo.getUpdatedAt()) // BaseEntity에 @LastModifiedDate가 있다고 가정
                .status(memo.getStatus().name())
                .build();
    }
}