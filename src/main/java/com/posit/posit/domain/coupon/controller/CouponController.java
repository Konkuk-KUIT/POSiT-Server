package com.posit.posit.domain.coupon.controller;

import com.posit.posit.domain.coupon.dto.request.CouponRedeemRequest;
import com.posit.posit.domain.coupon.dto.response.CouponRedeemResponse;
import com.posit.posit.domain.coupon.dto.response.MyCouponDetailResponse;
import com.posit.posit.domain.coupon.dto.response.MyCouponResponse;
import com.posit.posit.domain.coupon.entity.IssuedCouponStatus;
import com.posit.posit.domain.coupon.service.CouponService;
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

@Tag(name = "Coupon API", description = "쿠폰 관련 API") // 1. API 그룹 이름표
@RestController
@RequestMapping("/coupons") // 2. 공통 URL (/coupons) 묶음
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    // 보유 쿠폰 목록 조회
    // GET /coupons?status=ISSUED&size=10&cursor=105
    @Operation(summary = "보유 쿠폰 목록 조회", description = "상태별(사용가능/사용완료/만료) 쿠폰 목록을 커서 기반 페이징으로 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<MyCouponResponse>>> getMyCoupons(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestParam IssuedCouponStatus status,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long cursor
    ) {
        // 1. 서비스에서 Slice 받기
        Slice<MyCouponResponse> result = couponService.getMyCoupons(user.getId(), status, cursor, size);

        // 2. ApiResponse.success(Slice) 호출 -> data:List, meta:페이징 정보로 자동 변환
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    // 보유 쿠폰 상세 조회
    // GET /coupons/{couponId}
    @Operation(summary = "보유 쿠폰 상세 조회", description = "특정 쿠폰의 상세 정보를 조회합니다.")
    @GetMapping("/{couponId}")
    public ResponseEntity<ApiResponse<MyCouponDetailResponse>> getCouponDetail(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable Long couponId
    ) {
        MyCouponDetailResponse response = couponService.getCouponDetail(user.getId(), couponId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "쿠폰 사용하기", description = "가게에서 PIN 번호를 입력받아 쿠폰을 사용 처리합니다.")
    @PostMapping("/{couponId}/redeem")
    public ResponseEntity<ApiResponse<CouponRedeemResponse>> redeemCoupon(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable Long couponId,
            @RequestBody @Valid CouponRedeemRequest request
    ) {
        CouponRedeemResponse response = couponService.redeemCoupon(user.getId(), couponId, request.couponPin());
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}