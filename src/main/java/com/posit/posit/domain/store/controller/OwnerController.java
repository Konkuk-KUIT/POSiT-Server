package com.posit.posit.domain.store.controller;

import com.posit.posit.domain.coupon.dto.request.CouponTemplateUpdateRequest;
import com.posit.posit.domain.coupon.dto.response.CouponTemplateUpdateResponse;
import com.posit.posit.domain.memo.service.MemoService;
import com.posit.posit.domain.store.dto.request.*;
import com.posit.posit.domain.store.dto.response.*;
import com.posit.posit.domain.store.service.OwnerService;
import com.posit.posit.domain.user.dto.UserPrincipal;
import com.posit.posit.global.response.ApiResponse;
import com.posit.posit.global.swagger.ApiErrorCodes;
import com.posit.posit.global.swagger.SwaggerErrorSet;
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
    private final MemoService memoService;

    // 1. 쿠폰 템플릿 등록
    @Operation(summary = "쿠폰 템플릿 등록", description = "사장님이 발급할 쿠폰의 템플릿(종류)을 등록합니다.")
    @PostMapping("/coupons")
    public ResponseEntity<ApiResponse<Long>> createCouponTemplate(
            @AuthenticationPrincipal UserPrincipal user, // 로그인한 사장님 정보
            @RequestBody @Valid CouponTemplateCreateRequest request
    ) {
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
    @ApiErrorCodes(SwaggerErrorSet.CREATE_CONCERN)
    @PostMapping("/owner/concerns")
    public ResponseEntity<ApiResponse<ConcernCreateResponse>> createConcern(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestBody @Valid ConcernCreateRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(ownerService.createConcern(user.getId(), request)));
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
    @ApiErrorCodes(SwaggerErrorSet.MEMO_ADOPT)
    @PostMapping("/memos/{memoId}/adopt")
    public ResponseEntity<ApiResponse<ConcernAdoptResponse>> adoptMemo(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable Long memoId,
            @RequestBody @Valid MemoAdoptRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(ownerService.adoptMemo(user.getId(), memoId, request)));
    }

    // 5-2. 답변 거절
    @Operation(summary = "답변 거절", description = "마음에 들지 않는 답변(메모)을 거절 처리합니다.")
    @ApiErrorCodes(SwaggerErrorSet.MEMO_REJECT)
    @PostMapping("/memos/{memoId}/reject")
    public ResponseEntity<ApiResponse<ConcernRejectResponse>> rejectMemo(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable Long memoId,
            @RequestBody @Valid MemoRejectRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(ownerService.rejectMemo(user.getId(), memoId, request)));
    }

    // 6. 사장님 홈 화면 (대시보드)
    @Operation(summary = "사장님 홈 화면 (대시보드)", description = "사장님 메인 화면에 필요한 정보들을 조회합니다.")
    @GetMapping("/owner/home")
    public ResponseEntity<ApiResponse<OwnerHomeResponse>> getOwnerHome(
            @AuthenticationPrincipal UserPrincipal user
    ) {
        OwnerHomeResponse response = ownerService.getOwnerHome(user.getId());
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
    @Operation(summary = "내가 올린 고민 목록 조회", description = "사장님이 작성한 고민 목록을 무한 스크롤로 조회합니다.")
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

    @Operation(summary = "채택 후 화면", description = "포짓 채택 후 화면 조회에 필요한 엔드포인트입니다.")
    @GetMapping("/memos/{memoId}/adoption")
    public ResponseEntity<ApiResponse<AdoptionResultResponse>> adoptionResult(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable Long memoId
    ) {
        return ResponseEntity.ok(ApiResponse.success(ownerService.getAdoptionResult(user.getId(), memoId)));
    }

    @Operation(summary = "내 가게 정보 전체 수정", description = "가게 등록과 동일한 포맷으로 데이터를 받아 기존 정보를 덮어씁니다.")
    @ApiErrorCodes(SwaggerErrorSet.STORE_UPDATE)
    @PutMapping("/owner/store")
    public ResponseEntity<ApiResponse<Long>> updateStore(
            @AuthenticationPrincipal UserPrincipal user, // 토큰에서 사장님 ID 추출
            @RequestBody @Valid StoreUpdateRequest request // 등록 때 썼던 DTO 재사용
    ) {
        // Service의 updateStore 메서드 호출 (아까 만든 '갈아끼우기' 로직)
        Long storeId = ownerService.updateStore(user.getId(), request);

        return ResponseEntity.ok(ApiResponse.success(storeId));
    }

    @Operation(summary = "내 가게 ID 반환", description = "로그인한 사장님의 가게 ID를 반환합니다.")
    @GetMapping("/owner/store-id")
    public ResponseEntity<ApiResponse<Long>> myStoreId(
            @AuthenticationPrincipal UserPrincipal user
    ) {
        return ResponseEntity.ok(ApiResponse.success(ownerService.getMyStoreId(user.getId())));
    }

    @Operation(summary = "가게 편의시설 수정", description = "가게의 편의시설을 수정합니다.")
    @ApiErrorCodes(SwaggerErrorSet.CONVINCE_UPDATE)
    @PatchMapping("/owner/store/convinces")
    public ResponseEntity<ApiResponse<ConvinceUpdate>> updateConvince(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestBody ConvinceUpdate request
    ) {
        return ResponseEntity.ok(ApiResponse.success(ownerService.updateConvince(user.getId(), request)));
    }
}