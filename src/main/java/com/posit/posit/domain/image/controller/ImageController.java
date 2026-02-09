package com.posit.posit.domain.image.controller;

import com.posit.posit.domain.image.dto.request.PresignedUrlRequest;
import com.posit.posit.domain.image.dto.response.PresignedUrlResponse;
import com.posit.posit.global.response.ApiResponse;
import com.posit.posit.global.s3.S3Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "Image API", description = "이미지 업로드 관련 API")
@RestController
@RequestMapping("/images")
@RequiredArgsConstructor
public class ImageController {

    private final S3Service s3Service;

    // 이미지 업로드용 Pre-signed URL 발급
    // POST /images/presigned-url
    @Operation(summary = "Pre-signed URL 발급", description = "S3에 이미지를 직접 업로드하기 위한 임시 URL(Pre-signed URL)을 발급받습니다.")
    @PostMapping("/presigned-url")
    public ResponseEntity<ApiResponse<PresignedUrlResponse>> getPresignedUrl(@RequestBody PresignedUrlRequest request) {

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