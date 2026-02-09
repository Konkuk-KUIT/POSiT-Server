package com.posit.posit.domain.memo.controller;

import com.posit.posit.domain.memo.dto.request.MemoCreateRequest;
import com.posit.posit.domain.memo.dto.request.MemoUpdateRequest;
import com.posit.posit.domain.memo.dto.response.MemoCreateResponse;
import com.posit.posit.domain.memo.dto.response.MemoUpdateResponse;
import com.posit.posit.domain.memo.dto.response.MyMemoDetailResponse;
import com.posit.posit.domain.memo.dto.response.MyMemoListResponse;
import com.posit.posit.domain.memo.entity.MemoStatus;
import com.posit.posit.domain.memo.entity.MemoType;
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

    // 내가 쓴 메모 목록 조회 (게스트용)
    // 1. 대기중인 답변 탭: GET /memos/me?type=ANSWER&status=REVIEWING
    // 2. 채택된 답변 탭:   GET /memos/me?type=ANSWER&status=ADOPTED
    // 3. 자유 메모함 탭:   GET /memos/me?type=FREE&status=REVIEWING
    @Operation(summary = "내가 쓴 메모 조회", description = "게스트가 자신이 작성한 메모 목록을 타입(ANSWER/FREE)과 상태(REVIEWING/ADOPTED)별로 조회합니다.")
    @GetMapping("/memos/me")
    public ResponseEntity<ApiResponse<MyMemoListResponse>> getMyMemos(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestParam(required = false) MemoType type,     // ANSWER, FREE
            @RequestParam(required = false) MemoStatus status, // REVIEWING, ADOPTED
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long cursorId
    ) {
        MyMemoListResponse response = memoService.getMyMemos(user.getId(), type, status, cursorId, size);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 내 메모 상세 조회
    // GET /memos/me/{memoId}
    @Operation(summary = "내 메모 상세 조회", description = "특정 메모의 상세 내용(고민 내용 + 제목/본문 + 사장님 답글)을 조회합니다.")
    @GetMapping("/memos/me/{memoId}")
    public ResponseEntity<ApiResponse<MyMemoDetailResponse>> getMemoDetail(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable Long memoId
    ) {
        MyMemoDetailResponse response = memoService.getMemoDetail(user.getId(), memoId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}