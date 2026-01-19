package com.posit.posit.global.response;

public record Meta(
        String orderType,
        String nextCursor,
        Boolean hasNext
) {
}
