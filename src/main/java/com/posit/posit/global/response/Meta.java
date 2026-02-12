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

    public static Meta from(Long cursorId) {
        if (cursorId == null) {
            // 커서가 없으면 다음 페이지도 없음
            return new Meta(null, null, false);
        }
        return new Meta(
                null,
                String.valueOf(cursorId), // Long -> String 변환
                true // 커서 ID가 존재한다는 건 다음 페이지가 있다는 뜻
        );
    }
}