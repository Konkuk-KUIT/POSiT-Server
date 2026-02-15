package com.posit.posit.domain.memo.dto.response;

import com.posit.posit.domain.memo.entity.Memo;
import com.posit.posit.domain.memo.entity.MemoImage;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class MemoCreateResponse {

    private Long memoId;
    private String memoType;
    private LocalDateTime createdAt;
    private String status;

    // [추가] 업로드된 이미지 URL 리스트
    private List<String> images;

    public static MemoCreateResponse from(Memo memo) {

        // MemoImage 엔티티 리스트 -> String URL 리스트로 변환
        List<String> imageUrls = Collections.emptyList();
        if (memo.getImages() != null && !memo.getImages().isEmpty()) {
            imageUrls = memo.getImages().stream()
                    .map(MemoImage::getImageUrl) // MemoImage 객체에서 URL만 꺼내기
                    .collect(Collectors.toList());
        }

        return MemoCreateResponse.builder()
                .memoId(memo.getId())
                .memoType(memo.getMemoType().name())
                .createdAt(memo.getCreatedAt())
                .status(memo.getStatus().name())
                .images(imageUrls) // [추가] 여기에 담아서 반환
                .build();
    }
}