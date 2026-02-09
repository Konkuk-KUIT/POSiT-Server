package com.posit.posit.domain.coupon.controller;

import com.posit.posit.domain.coupon.dto.response.MyCouponDetailResponse;
import com.posit.posit.domain.coupon.dto.response.MyCouponListResponse;
import com.posit.posit.domain.coupon.entity.IssuedCouponStatus;
import com.posit.posit.domain.coupon.service.CouponService;
import com.posit.posit.domain.user.dto.UserPrincipal;
import com.posit.posit.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Coupon API", description = "쿠폰 관련 API") // 1. API 그룹 이름표
@RestController
@RequestMapping("/coupons") // 2. 공통 URL (/coupons) 묶음
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    // 보유 쿠폰 목록 조회
    // GET /coupons?status=ISSUED&size=20&cursor=105
    @Operation(summary = "보유 쿠폰 목록 조회", description = "상태별(사용가능/사용완료/만료) 쿠폰 목록을 커서 기반 페이징으로 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<MyCouponListResponse>> getMyCoupons(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestParam IssuedCouponStatus status, // 필수 파라미터
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Long cursor // optional
    ) {
        MyCouponListResponse response = couponService.getMyCoupons(user.getId(), status, cursor, size);
        return ResponseEntity.ok(ApiResponse.success(response));
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
}