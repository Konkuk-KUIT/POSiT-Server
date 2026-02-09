package com.posit.posit.domain.memo.controller;

import com.posit.posit.domain.memo.dto.request.MemoCreateRequest;
import com.posit.posit.domain.memo.dto.request.MemoUpdateRequest;
import com.posit.posit.domain.memo.dto.response.MemoCreateResponse;
import com.posit.posit.domain.memo.dto.response.MemoUpdateResponse;
import com.posit.posit.domain.memo.service.MemoService;
import com.posit.posit.domain.user.dto.UserPrincipal;
import com.posit.posit.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Memo API", description = "메모 관련 API")
@RestController
@RequiredArgsConstructor
public class MemoController {

    private final MemoService memoService;

    // 메모 등록 API
    // POST /stores/{storeId}/memos
    @Operation(summary = "메모 등록", description = "특정 가게에 대한 새로운 메모를 작성합니다.") // 2. API 설명 추가
    @PostMapping("/stores/{storeId}/memos")
    public ResponseEntity<ApiResponse<MemoCreateResponse>> createMemo(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable Long storeId,
            @RequestBody @Valid MemoCreateRequest request
    ) {
        MemoCreateResponse response = memoService.createMemo(user.getId(), storeId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 메모 수정 API
    // PATCH /memos/{memoId}
    @Operation(summary = "메모 수정", description = "기존 메모의 내용을 수정합니다.") // 2. API 설명 추가
    @PatchMapping("/memos/{memoId}")
    public ResponseEntity<ApiResponse<MemoUpdateResponse>> updateMemo(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable Long memoId,
            @RequestBody MemoUpdateRequest request
    ) {
        MemoUpdateResponse response = memoService.updateMemo(user.getId(), memoId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}