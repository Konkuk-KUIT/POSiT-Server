package com.posit.posit.domain.store.dto.response;

public record ConcernAdoptResponse(
        String concernTitle,
        String writer,
        String adoptedAt,
        String reward
) {
}
