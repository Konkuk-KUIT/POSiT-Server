package com.posit.posit.domain.store.dto.response;

public record ConcernRejectResponse(
        String concernTitle,
        String writer,
        String rejectedAt
) {
}
