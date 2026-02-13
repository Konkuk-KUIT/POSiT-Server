package com.posit.posit.domain.store.dto.response;

import com.posit.posit.domain.concern.entity.Concern;
import com.posit.posit.domain.memo.entity.Memo;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class ConcernDetailResponse {

    // --- [상단] 고민글 영역 ---
    private Long concernId;
    private String concernContent; // 제목 없이 내용만 표시
    private LocalDateTime createdAt;

    // --- [하단] 답변(메모) 리스트 영역 ---
    private List<MemoItem> memos;

    @Getter
    @Builder
    public static class MemoItem {
        private Long memoId;
        private String writerName; // "subinn"

        // ★ UI 핵심: 제목과 내용 분리
        private String title;      // 굵은 글씨 (예: "노란 조명 대신...")
        private String content;    // 작은 글씨 (예: "사진 찍으면...")

        private LocalDateTime createdAt; // "1일 전"

        public static MemoItem from(Memo memo) {
            return MemoItem.builder()
                    .memoId(memo.getId())
                    .writerName(memo.getUser().getName())
                    .title(memo.getTitle())      // 엔티티의 title -> 굵은 글씨
                    .content(memo.getContent())  // 엔티티의 content -> 작은 글씨
                    .createdAt(memo.getCreatedAt())
                    .build();
        }
    }

    public static ConcernDetailResponse of(Concern concern, List<Memo> memos) {
        return ConcernDetailResponse.builder()
                .concernId(concern.getId())
                .concernContent(concern.getContent())
                .createdAt(concern.getCreatedAt())
                .memos(memos.stream()
                        .map(MemoItem::from)
                        .collect(Collectors.toList()))
                .build();
    }
}