package com.posit.posit.domain.store.dto.response;

import com.posit.posit.domain.memo.entity.Memo;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
//개별 아이템
public class InboxMemoResponse {
    private Long id;
    private String type;      // "ANSWER" or "FREE"
    private String title;
    private String content;   // 미리보기용
    private LocalDateTime createdAt;

    // Entity -> DTO 변환 메서드
    public static InboxMemoResponse from(Memo memo) {
        return InboxMemoResponse.builder()
                .id(memo.getId())
                .type(memo.getMemoType().name()) // ANSWER, FREE
                .title(memo.getTitle())
                .content(memo.getContent())
                .createdAt(memo.getCreatedAt())
                .build();
    }
}