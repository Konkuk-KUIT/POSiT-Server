package com.posit.posit.global.response;

import org.springframework.data.domain.Slice;

public record Meta(
        String orderType,
        String nextCursor,
        Boolean hasNext
) {
    public static Meta from(Slice<?> slice) {
        return new Meta(
                null,
                null,
                slice.hasNext()
        );
    }
}