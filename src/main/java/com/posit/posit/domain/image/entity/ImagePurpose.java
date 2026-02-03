package com.posit.posit.domain.image.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ImagePurpose {
    MEMO_IMAGE("memos"),    // 저장 경로: uploads/memos/...
    STORE_IMAGE("stores"),  // 저장 경로: uploads/stores/...
    PROFILE_IMAGE("profiles");

    private final String subPath;
}