package com.posit.posit.domain.image.dto.request;

import com.posit.posit.domain.image.entity.ImagePurpose;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class PresignedUrlRequest {

    private ImagePurpose purpose; // MEMO_IMAGE, STORE_IMAGE
    private List<FileInfo> files;

    @Getter
    @NoArgsConstructor
    public static class FileInfo {
        private String fileName;
        private String contentType;   // image/jpeg
        private long contentLength;   // 120345 (byte)
    }
}