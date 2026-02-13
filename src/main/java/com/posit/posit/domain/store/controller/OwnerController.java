package com.posit.posit.domain.store.controller;

import com.posit.posit.domain.coupon.dto.request.CouponTemplateUpdateRequest;
import com.posit.posit.domain.coupon.dto.response.CouponTemplateUpdateResponse;
import com.posit.posit.domain.store.dto.request.*;
import com.posit.posit.domain.store.dto.response.*;
import com.posit.posit.domain.store.service.OwnerService;
import com.posit.posit.domain.user.dto.UserPrincipal;
import com.posit.posit.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Owner API", description = "사장님 전용 API (가게, 고민, 쿠폰 관리 등)") // 1. API 그룹 이름표
@RestController
@RequiredArgsConstructor
public class OwnerController {

    private final OwnerService ownerService;

    // 1. 쿠폰 템플릿 등록
    @Operation(summary = "쿠폰 템플릿 등록", description = "사장님이 발급할 쿠폰의 템플릿(종류)을 등록합니다.")
    @PostMapping("/coupons")
    public ResponseEntity<ApiResponse<Long>> createCouponTemplate(
            @AuthenticationPrincipal UserPrincipal user, // 로그인한 사장님 정보
            @RequestBody @Valid CouponTemplateCreateRequest request
    ) {
        // user.getId()를 넘김
        Long templateId = ownerService.createCouponTemplate(user.getId(), request);
        return ResponseEntity.ok(ApiResponse.success(templateId));
    }

    // 2. 쿠폰 템플릿 목록 조회
    @Operation(summary = "쿠폰 템플릿 목록 조회", description = "내가 만든 쿠폰 템플릿 목록을 조회합니다.")
    @GetMapping("/owner/coupon-templates")
    public ResponseEntity<ApiResponse<List<CouponTemplateResponse>>> getCouponTemplates(
            @AuthenticationPrincipal UserPrincipal user
    ) {
        // 내(사장님)가 만든 템플릿만 조회
        List<CouponTemplateResponse> list = ownerService.getCouponTemplates(user.getId());
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    // 3. 고민 등록
    @Operation(summary = "고민 등록", description = "가게에 대한 고민글을 작성합니다.")
    @PostMapping("/stores/concerns")
    public ResponseEntity<ApiResponse<Long>> createConcern(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestBody @Valid ConcernCreateRequest request
    ) {
        // user.getId()를 넘겨서 서비스에서 본인 확인을 하게 합니다.
        Long concernId = ownerService.createConcern(user.getId(), request);

        return ResponseEntity.ok(ApiResponse.success(concernId));
    }

    // 4. 수신함 목록 조회
    // GET /owner/inbox?storeId=1&tab=ANSWER&cursorId=100&limit=10
    @Operation(summary = "수신함 목록 조회", description = "답변이 달린 고민이나, 쿠폰 사용 알림 등을 조회합니다.")
    @GetMapping("/owner/inbox")
    public ApiResponse<List<InboxMemoResponse>> getInbox(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestParam String tab,
            @RequestParam(required = false) Long cursorId,
            @RequestParam(defaultValue = "10") int limit
    ) {
        // 1. 서비스에서 Slice(데이터 뭉치)를 받음
        Slice<InboxMemoResponse> result = ownerService.getInbox(user.getId(), tab, cursorId, limit);

        // 2. ApiResponse.success(Slice) 호출
        // -> data에는 List가, meta에는 페이징 정보가 자동으로 들어갑니다.
        return ApiResponse.success(result);
    }

    // 5-1. 답변 채택
    @Operation(summary = "답변 채택", description = "마음에 드는 답변(메모)을 채택하고 쿠폰을 발급합니다.")
    @PostMapping("/memos/{memoId}/adopt")
    public ResponseEntity<ApiResponse<String>> adoptMemo(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable Long memoId,
            @RequestBody @Valid MemoAdoptRequest request
    ) {
        ownerService.adoptMemo(user.getId(), memoId, request);
        return ResponseEntity.ok(ApiResponse.success("채택 완료 및 쿠폰 발급 성공"));
    }

    // 5-2. 답변 거절
    @Operation(summary = "답변 거절", description = "마음에 들지 않는 답변(메모)을 거절 처리합니다.")
    @PostMapping("/memos/{memoId}/reject")
    public ResponseEntity<ApiResponse<String>> rejectMemo(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable Long memoId,
            @RequestBody @Valid MemoRejectRequest request
    ) {
        ownerService.rejectMemo(user.getId(), memoId, request);
        return ResponseEntity.ok(ApiResponse.success("거절 처리 완료"));
    }

    // 6. 사장님 홈 화면 (대시보드)
    @Operation(summary = "사장님 홈 화면 (대시보드)", description = "사장님 메인 화면에 필요한 정보들을 조회합니다.")
    @GetMapping("/owner/home")
    public ResponseEntity<ApiResponse<OwnerHomeResponse>> getOwnerHome(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestParam Long storeId
    ) {
        OwnerHomeResponse response = ownerService.getOwnerHome(user.getId(), storeId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 0. 가게 등록
    @Operation(summary = "가게 등록", description = "사장님의 가게 정보를 등록합니다.")
    @PostMapping("/stores")
    public ResponseEntity<ApiResponse<Long>> registerStore(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestBody @Valid StoreRegisterRequest request
    ) {
        Long storeId = ownerService.registerStore(user.getId(), request);
        return ResponseEntity.ok(ApiResponse.success(storeId)); // storeId 반환 (또는 성공 메시지)
    }

    // 고민 수정
    // PATCH /concerns/{concernId}
    @Operation(summary = "고민 수정", description = "작성한 고민 내용을 수정합니다.")
    @PatchMapping("/concerns/{concernId}")
    public ResponseEntity<ApiResponse<ConcernUpdateResponse>> updateConcern(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable Long concernId,
            @RequestBody @Valid ConcernUpdateRequest request
    ) {
        ConcernUpdateResponse response = ownerService.updateConcern(user.getId(), concernId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 고민 상세 조회
    // GET /concerns/{concernId}
    @Operation(summary = "고민 상세 조회", description = "특정 고민의 상세 내용을 조회합니다.")
    @GetMapping("/concerns/{concernId}")
    public ResponseEntity<ApiResponse<ConcernDetailResponse>> getConcernDetail(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable Long concernId
    ) {
        ConcernDetailResponse response = ownerService.getConcernDetail(user.getId(), concernId);

        // 상세 조회는 페이징이 없으므로 meta 없이 success 호출
        return ResponseEntity.ok(ApiResponse.success(response));
    }


    // 11. 쿠폰 템플릿 수정
    // PATCH /coupon-templates/{templateId}
    @Operation(summary = "쿠폰 템플릿 수정", description = "등록된 쿠폰 템플릿 정보를 수정합니다.")
    @PatchMapping("/coupon-templates/{templateId}")
    public ResponseEntity<ApiResponse<CouponTemplateUpdateResponse>> updateCouponTemplate(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable Long templateId,
            @RequestBody @Valid CouponTemplateUpdateRequest request
    ) {
        CouponTemplateUpdateResponse response = ownerService.updateCouponTemplate(user.getId(), templateId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 12. 메모 상세 조회
    // GET /memos/{memoId}?type=ANSWER
    @Operation(summary = "메모 상세 조회", description = "특정 메모(답변)의 상세 내용을 조회합니다.")
    @GetMapping("/memos/{memoId}")
    public ResponseEntity<ApiResponse<MemoDetailResponse>> getMemoDetail(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable Long memoId
    ) {
        // 1. 서비스 호출 (이미 내부에서 권한/존재 여부 체크함)
        MemoDetailResponse response = ownerService.getMemoDetail(user.getId(), memoId);

        // 2. 성공 응답
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 13. 쿠폰 관리 (통계 + 목록)
    // GET /owner/coupon-management?size=10&cursorId=123
    @Operation(summary = "쿠폰 관리 (통계 + 목록)", description = "발급된 쿠폰들의 통계와 목록을 관리합니다.")
    @GetMapping("/owner/coupon-management")
    public ResponseEntity<ApiResponse<CouponManagementResponse>> getCouponManagement(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long cursorId
    ) {
        // 1. Service 호출
        CouponManagementResponse response = ownerService.getCouponManagement(user.getId(), cursorId, size);

        // 2. ApiResponse 생성
        // response 객체는 @JsonIgnore 때문에 cursor 정보가 JSON에서 빠져있음 (깔끔!)
        // 대신 getNextCursorId()로 값을 꺼내서 ApiResponse 껍데기에 전달
        return ResponseEntity.ok(ApiResponse.success(response, response.getNextCursorId()));
    }

    //고민들 조회
    @Operation(summary = "내가 올린 고민 목록 조회", description = "사장님이 작성한 고민 목록을 무한 스크롤로 조회합니다. (제목 자동 생성)")
    @GetMapping("/concerns/mine")
    public ResponseEntity<ApiResponse<OwnerConcernListResponse>> getMyConcerns(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestParam(required = false) Long cursorId,
            @RequestParam(defaultValue = "10") int size
    ) {
        OwnerConcernListResponse response = ownerService.getMyConcerns(user.getId(), cursorId, size);

        // ApiResponse.success(data, cursorId) -> 이 메서드는 이전에 만든 것 사용
        return ResponseEntity.ok(ApiResponse.success(response, response.getNextCursorId()));
    }

    // 가게 pin 번호 수정
    @Operation(summary = "쿠폰 비밀번호 설정", description = "쿠폰 비밀번호를 설정, 수정합니다.")
    @PatchMapping("/owner/coupon-pin")
    public ResponseEntity<ApiResponse<Void>> updatePin(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestBody @Valid StorePinUpdateRequest request
    ) {
        ownerService.updateStorePin(user.getId(), request);
        return ResponseEntity.ok(ApiResponse.success());
    }

    @Operation(summary = "현재 쿠폰 비밀번호 검증", description = "현재 쿠폰 비밀번호를 검증합니다.")
    @PostMapping("/owner/coupon-pin/verify")
    public ResponseEntity<ApiResponse<Void>> verifyPin(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestBody @Valid StorePinVerifyRequest request
    ) {
        ownerService.verifyStorePin(user.getId(), request);
        return ResponseEntity.ok(ApiResponse.success());
    }
}