package com.posit.posit.domain.store.dto.response;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.posit.posit.domain.concern.entity.Concern;
import com.posit.posit.domain.concern.entity.ConcernStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class OwnerConcernListResponse {

    private List<ConcernItem> concerns;

    @JsonIgnore // JSON 결과에는 포함 안 됨 (Controller에서 Meta로 뺌)
    private Long nextCursorId;

    @Getter
    @Builder
    public static class ConcernItem {
        private Long concernId;
        private String title;       //  UI용 제목 (내용 앞부분 자름)
        private String content;     // 전체 내용
        private long commentCount;  // 댓글 수
        private LocalDateTime createdAt;
        private boolean isResolved; // 해결 여부

        public static ConcernItem from(Concern concern, long count) {
            String originalContent = concern.getContent();

            // [로직] 내용이 50자보다 길면 자르고 '...' 붙임
            String generatedTitle = "";
            if (originalContent != null) {
                generatedTitle = originalContent.length() > 50
                        ? originalContent.substring(0, 50) + "..."
                        : originalContent;
            }

            return ConcernItem.builder()
                    .concernId(concern.getId())
                    .title(generatedTitle)
                    .content(originalContent)
                    .commentCount(count)
                    .createdAt(concern.getCreatedAt())
                    .isResolved(concern.getStatus() == ConcernStatus.CLOSED)
                    .build();
        }
    }
}