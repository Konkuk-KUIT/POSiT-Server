package com.posit.posit.domain.store.dto.response;

import com.posit.posit.domain.memo.entity.MemoStatus;

public record ConcernAdoptResponse(
        Long memoId,
        MemoStatus status
) {
}
