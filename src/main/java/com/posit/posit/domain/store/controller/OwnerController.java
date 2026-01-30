package com.posit.posit.domain.store.controller;

import com.posit.posit.domain.coupon.dto.request.CouponUseRequest;
import com.posit.posit.domain.store.dto.request.*;
import com.posit.posit.domain.store.dto.response.CouponTemplateResponse;
import com.posit.posit.domain.store.dto.response.InboxResponse;
import com.posit.posit.domain.store.dto.response.OwnerHomeResponse;
import com.posit.posit.domain.store.service.OwnerService;
import com.posit.posit.domain.user.dto.UserPrincipal;
import com.posit.posit.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class OwnerController {

    private final OwnerService ownerService;

    // 1. 쿠폰 템플릿 등록
    @PostMapping("/coupons")
    public ResponseEntity<?> createCouponTemplate(
            @AuthenticationPrincipal com.posit.posit.domain.user.dto.UserPrincipal user, // 로그인한 사장님 정보
            @RequestBody @Valid CouponTemplateCreateRequest request
    ) {
        // user.getId()를 넘김
        Long templateId = ownerService.createCouponTemplate(user.getId(), request);
        return ResponseEntity.ok(ApiResponse.success(templateId));
    }

    // 2. 쿠폰 템플릿 목록 조회
    @GetMapping("/owner/coupon-templates")
    public ResponseEntity<?> getCouponTemplates(
            @AuthenticationPrincipal UserPrincipal user
    ) {
        // 내(사장님)가 만든 템플릿만 조회
        List<CouponTemplateResponse> list = ownerService.getCouponTemplates(user.getId());
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    // 3. 고민 등록
    @PostMapping("/stores/{storeId}/concerns")
    public ResponseEntity<?> createConcern(
            @PathVariable Long storeId,
            @AuthenticationPrincipal UserPrincipal user,
            @RequestBody @Valid ConcernCreateRequest request
    ) {
        // user.getId()를 넘겨서 서비스에서 본인 확인을 하게 합니다.
        Long concernId = ownerService.createConcern(user.getId(), storeId, request);

        return ResponseEntity.ok(ApiResponse.success(concernId));
    }

    // 4. 수신함 목록 조회
    // GET /owner/inbox?storeId=1&tab=ANSWER&cursorId=100&limit=10
    @GetMapping("/owner/inbox")
    public ResponseEntity<?> getInbox(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestParam Long storeId,
            @RequestParam(defaultValue = "ANSWER") String tab, // 기본값: 고민 답변 탭
            @RequestParam(required = false) Long cursorId,        // 첫 페이지면 null
            @RequestParam(defaultValue = "10") int limit          // 기본 10개씩
    ) {
        InboxResponse response = ownerService.getInbox(storeId, tab, cursorId, limit);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 5-1. 답변 채택
    @PostMapping("/memos/{memoId}/adopt")
    public ResponseEntity<?> adoptMemo(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable Long memoId,
            @RequestBody @Valid MemoAdoptRequest request
    ) {
        ownerService.adoptMemo(user.getId(), memoId, request);
        return ResponseEntity.ok(ApiResponse.success("채택 완료 및 쿠폰 발급 성공"));
    }

    // 5-2. 답변 거절
    @PostMapping("/memos/{memoId}/reject")
    public ResponseEntity<?> rejectMemo(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable Long memoId,
            @RequestBody @Valid MemoRejectRequest request
    ) {
        ownerService.rejectMemo(user.getId(), memoId, request);
        return ResponseEntity.ok(ApiResponse.success("거절 처리 완료"));
    }

    // 6. 사장님 홈 화면 (대시보드)
    @GetMapping("/owner/home")
    public ResponseEntity<?> getOwnerHome(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestParam Long storeId
    ) {
        OwnerHomeResponse response = ownerService.getOwnerHome(user.getId(), storeId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 7. 쿠폰 사용 하기 (POST)
    @PostMapping("/coupons/{couponId}/use")
    public ResponseEntity<?> useCoupon(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable Long couponId,
            @RequestBody @Valid CouponUseRequest request // Body로 비밀번호 받기
    ) {
        ownerService.useCoupon(user.getId(), couponId, request);
        return ResponseEntity.ok(ApiResponse.success("쿠폰 사용이 완료되었습니다."));
    }

    // 0. 가게 등록
    @PostMapping("/stores")
    public ResponseEntity<?> registerStore(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestBody @Valid StoreRegisterRequest request
    ) {
        Long storeId = ownerService.registerStore(user.getId(), request);
        return ResponseEntity.ok(ApiResponse.success(storeId)); // storeId 반환 (또는 성공 메시지)
    }
}