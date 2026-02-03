package com.posit.posit.domain.image.controller;

import com.posit.posit.domain.image.dto.request.PresignedUrlRequest;
import com.posit.posit.domain.image.dto.response.PresignedUrlResponse;
import com.posit.posit.domain.image.entity.ImagePurpose;

import com.posit.posit.global.response.ApiResponse;
import com.posit.posit.global.s3.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class ImageController {

    private final S3Service s3Service;

    // 이미지 업로드용 Pre-signed URL 발급
    // POST /images/presigned-url
    @PostMapping("/images/presigned-url")
    public ResponseEntity<?> getPresignedUrl(@RequestBody PresignedUrlRequest request) {

        // 요청된 파일 목록을 순회하며 URL 생성
        List<PresignedUrlResponse.PresignedUrlItem> items = request.getFiles().stream()
                .map(file -> s3Service.getPresignedUrl(
                        request.getPurpose().getSubPath(), // memos, stores 등
                        file.getFileName(),
                        file.getContentType()
                ))
                .collect(Collectors.toList());

        PresignedUrlResponse response = PresignedUrlResponse.builder()
                .items(items)
                .build();

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}