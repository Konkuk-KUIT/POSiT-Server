package com.posit.posit.domain.store.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
//전체 응답 껍데기
public class InboxResponse {
    private List<InboxMemoResponse> memos;
    private Meta meta;

    @Getter
    @Builder
    public static class Meta {
        private Long nextCursor;
        private boolean hasNext;
    }
}