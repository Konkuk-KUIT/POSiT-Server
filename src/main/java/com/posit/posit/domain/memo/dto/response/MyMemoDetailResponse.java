package com.posit.posit.domain.memo.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MyMemoDetailResponse {

    private Long memoId;
    private Long storeId;
    private String storeName;

    // 1. 사장님 고민 (상단 분홍 박스) - Nullable
    private String concernContent;

    // 2. 내 메모 (중앙/상단 흰색 박스)
    private String memoTitle;     // 제목 (굵은 글씨)
    private String memoContent;   // 본문 (일반 글씨)

    // 3. 사장님 답글 (하단 말풍선) - Nullable
    private String ownerReply;

    private String status;        // REVIEWING, ADOPTED, REJECTED
    private String createdAt;
}