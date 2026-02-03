package com.posit.posit.domain.store.dto.response;

import com.posit.posit.domain.memo.entity.Memo;
import com.posit.posit.domain.memo.entity.MemoType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class MemoDetailResponse {

    private Long memoId;
    private String memoType;

    private OriginalConcern originalConcern;
    private String freeType;

    private String title;
    private String content;
    private String imageUrl;
    private String status;
    private LocalDateTime createdAt;

    private WriterInfo writer;

    @Getter @Builder
    public static class OriginalConcern {
        private Long concernId;
        private String content;
    }

    @Getter @Builder
    public static class WriterInfo {
        private String name;
    }

    public static MemoDetailResponse from(Memo memo) {

        OriginalConcern concernDto = null;
        if (memo.getMemoType() == MemoType.ANSWER && memo.getConcern() != null) {
            concernDto = OriginalConcern.builder()
                    .concernId(memo.getConcern().getId())
                    .content(memo.getConcern().getContent())
                    .build();
        }

        String freeTypeStr = null;
        if (memo.getMemoType() == MemoType.FREE && memo.getFreeType() != null) {
            freeTypeStr = memo.getFreeType().name();
        }

        return MemoDetailResponse.builder()
                .memoId(memo.getId())
                .memoType(memo.getMemoType().name())
                .originalConcern(concernDto)
                .freeType(freeTypeStr)
                .title(memo.getTitle())
                .content(memo.getContent())
                .imageUrl(memo.getImage())
                .status(memo.getStatus().name())
                .createdAt(memo.getCreatedAt())
                .writer(WriterInfo.builder()
                        .name(memo.getUser().getLoginId())
                        .build())
                .build();
    }
}