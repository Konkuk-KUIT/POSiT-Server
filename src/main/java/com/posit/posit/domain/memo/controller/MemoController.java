package com.posit.posit.domain.memo.controller;

import com.posit.posit.domain.memo.dto.request.MemoCreateRequest;
import com.posit.posit.domain.memo.dto.request.MemoUpdateRequest;
import com.posit.posit.domain.memo.dto.response.MemoCreateResponse;
import com.posit.posit.domain.memo.dto.response.MemoUpdateResponse;
import com.posit.posit.domain.memo.service.MemoService;
import com.posit.posit.domain.user.dto.UserPrincipal;
import com.posit.posit.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class MemoController {

    private final MemoService memoService;

    // 메모 등록 API
    // POST /stores/{storeId}/memos
    @PostMapping("/stores/{storeId}/memos")
    public ResponseEntity<?> createMemo(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable Long storeId,
            @RequestBody @Valid MemoCreateRequest request
    ) {
        MemoCreateResponse response = memoService.createMemo(user.getId(), storeId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 메모 수정 API
    // PATCH /memos/{memoId}
    @PatchMapping("/memos/{memoId}")
    public ResponseEntity<?> updateMemo(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable Long memoId,
            @RequestBody MemoUpdateRequest request
    ) {
        MemoUpdateResponse response = memoService.updateMemo(user.getId(), memoId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
