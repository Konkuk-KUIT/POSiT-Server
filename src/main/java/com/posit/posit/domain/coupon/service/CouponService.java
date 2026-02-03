package com.posit.posit.domain.coupon.service;

import com.posit.posit.domain.coupon.dto.response.MyCouponDetailResponse;
import com.posit.posit.domain.coupon.dto.response.MyCouponListResponse;
import com.posit.posit.domain.coupon.entity.IssuedCoupon;
import com.posit.posit.domain.coupon.entity.IssuedCouponStatus;
import com.posit.posit.domain.coupon.repository.IssuedCouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CouponService {

    private final IssuedCouponRepository issuedCouponRepository;

    public MyCouponListResponse getMyCoupons(Long userId, IssuedCouponStatus status, Long cursorId, int size) {

        // 1. 다음 페이지 확인을 위해 size + 1개 조회
        PageRequest pageRequest = PageRequest.of(0, size + 1);
        List<IssuedCoupon> coupons;

        if (cursorId == null) {
            coupons = issuedCouponRepository.findAllMyCouponsFirst(userId, status, pageRequest);
        } else {
            coupons = issuedCouponRepository.findAllMyCouponsNext(userId, status, cursorId, pageRequest);
        }

        // 2. hasNext 판단 및 데이터 자르기
        boolean hasNext = false;
        Long nextCursor = null;

        if (coupons.size() > size) {
            hasNext = true;
            coupons.remove(size); // +1개 제거
            nextCursor = coupons.get(coupons.size() - 1).getId(); // 다음 커서 ID
        }

        // 3. DTO 변환
        List<MyCouponListResponse.CouponInfo> couponInfos = coupons.stream()
                .map(MyCouponListResponse.CouponInfo::from)
                .toList();

        // 4. Meta 정보 생성 및 반환
        return MyCouponListResponse.builder()
                .coupons(couponInfos)
                .meta(MyCouponListResponse.Meta.builder()
                        .nextCursor(nextCursor)
                        .hasNext(hasNext)
                        .build())
                .build();
    }

    // 쿠폰 상세 조회
    public MyCouponDetailResponse getCouponDetail(Long userId, Long couponId) {

        // 내 쿠폰인지 확인하면서 조회 (없으면 예외 발생)
        IssuedCoupon coupon = issuedCouponRepository.findByIdAndUserId(couponId, userId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 쿠폰이거나 접근 권한이 없습니다."));

        return MyCouponDetailResponse.from(coupon);
    }
}