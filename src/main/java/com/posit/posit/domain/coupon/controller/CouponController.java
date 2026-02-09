package com.posit.posit.domain.coupon.controller;

import com.posit.posit.domain.coupon.dto.response.MyCouponDetailResponse;
import com.posit.posit.domain.coupon.dto.response.MyCouponListResponse;
import com.posit.posit.domain.coupon.entity.IssuedCouponStatus;
import com.posit.posit.domain.coupon.service.CouponService;
import com.posit.posit.domain.user.dto.UserPrincipal;
import com.posit.posit.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    // 보유 쿠폰 목록 조회
    // GET /coupons?status=ISSUED&size=20&cursor=105
    @GetMapping("/coupons")
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
    @GetMapping("/coupons/{couponId}")
    public ResponseEntity<?> getCouponDetail(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable Long couponId
    ) {
        MyCouponDetailResponse response = couponService.getCouponDetail(user.getId(), couponId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}