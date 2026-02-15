package com.posit.posit.domain.memo.dto.request;

import com.posit.posit.domain.memo.entity.FreeType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class MemoUpdateRequest {

    private String title;
    private String content;
    private FreeType freeType; // FREE 타입일 때만 유효 (TIP, MARKETING...)

    private List<String> imageKeys;
}