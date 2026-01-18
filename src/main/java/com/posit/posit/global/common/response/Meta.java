package com.posit.posit.global.common.response;

public record Meta(
        String orderType,
        String nextCursor,
        Boolean hasNext
) {
}
