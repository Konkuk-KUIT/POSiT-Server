package com.posit.posit.domain.memo.dto.request;

import com.posit.posit.domain.memo.entity.FreeType;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemoUpdateRequest {

    private String title;
    private String content;
    private String imageUrl; // JSON 예시에 맞춰 이름 설정
    private FreeType freeType; // FREE 타입일 때만 유효 (TIP, MARKETING...)
}