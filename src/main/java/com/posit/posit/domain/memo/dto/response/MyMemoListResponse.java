package com.posit.posit.domain.memo.dto.response;

import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder
public class MyMemoListResponse {
    private List<MyMemoItem> memos;
    private Long nextCursorId; // 다음 페이지 커서
    private boolean hasNext;   // 다음 페이지 존재 여부

    @Getter
    @Builder
    public static class MyMemoItem {
        private Long memoId;
        private String storeName;    // 가게 이름
        private String category;     // "고민 답변" or "자유 메모"
        private String content;      // 메모 내용
        private String status;       // REVIEWING, ADOPTED 등
        private String createdAt;    // 작성일
        private boolean isRead;      // (DB에 없어서 false 고정)
    }
}