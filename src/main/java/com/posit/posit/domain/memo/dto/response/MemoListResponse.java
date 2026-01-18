package com.posit.posit.domain.memo.dto.response;

public record MemoListResponse(
        List<MemoSummaryResponse> memos,
        CursorMeta meta
) {
}