package com.posit.posit.domain.memo.dto.request;

import com.posit.posit.domain.memo.entity.FreeType;
import com.posit.posit.domain.memo.entity.MemoType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class MemoCreateRequest {

    @NotNull(message = "메모 타입은 필수입니다. (ANSWER 또는 FREE)")
    private MemoType memoType;

    private Long concernId; // ANSWER일 때 필수

    private FreeType freeType; // FREE일 때 필수 (TIP, MARKETING...)

    @NotBlank(message = "제목을 입력해주세요.")
    @Size(max = 50)
    private String title;

    @NotBlank(message = "내용을 입력해주세요.")
    @Size(max = 150)
    private String content;

    private List<ImageDto> images; // 이미지 리스트

    @Getter
    @NoArgsConstructor
    public static class ImageDto {
        private String imageKey;
        private int order;
    }
}