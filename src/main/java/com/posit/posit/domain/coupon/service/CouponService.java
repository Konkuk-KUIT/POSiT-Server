package com.posit.posit.domain.coupon.service;

import com.posit.posit.domain.coupon.dto.response.CouponRedeemResponse;
import com.posit.posit.domain.coupon.dto.response.MyCouponDetailResponse;
import com.posit.posit.domain.coupon.dto.response.MyCouponResponse;
import com.posit.posit.domain.coupon.entity.IssuedCoupon;
import com.posit.posit.domain.coupon.entity.IssuedCouponStatus;
import com.posit.posit.domain.coupon.repository.IssuedCouponRepository;
import com.posit.posit.domain.store.entity.Store;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CouponService {

    private final IssuedCouponRepository issuedCouponRepository;

    @Transactional
    public Slice<MyCouponResponse> getMyCoupons(Long userId, IssuedCouponStatus status, Long cursorId, int size) {

        Pageable pageable = PageRequest.of(0, size + 1);
        List<IssuedCoupon> coupons;

        if (cursorId == null) {
            coupons = issuedCouponRepository.findAllMyCouponsFirst(userId, status, pageable);
        } else {
            coupons = issuedCouponRepository.findAllMyCouponsNext(userId, status, cursorId, pageable);
        }

        boolean hasNext = false;
        if (coupons.size() > size) {
            hasNext = true;
            coupons.remove(size);
        }

        List<MyCouponResponse> dtos = coupons.stream()
                .map(MyCouponResponse::from)
                .collect(Collectors.toList());

        return new SliceImpl<>(dtos, pageable, hasNext);
    }

    // 쿠폰 상세 조회
    public MyCouponDetailResponse getCouponDetail(Long userId, Long couponId) {

        // 내 쿠폰인지 확인하면서 조회 (없으면 예외 발생)
        IssuedCoupon coupon = issuedCouponRepository.findByIdAndUserId(couponId, userId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 쿠폰이거나 접근 권한이 없습니다."));

        return MyCouponDetailResponse.from(coupon);
    }

    private final PasswordEncoder passwordEncoder; // SecurityConfig에 등록된 빈 주입 필요
    @Transactional
    public CouponRedeemResponse redeemCoupon(Long userId, Long couponId, String inputPin) {
        // 1. 쿠폰 조회 (내 쿠폰인지 확인)
        IssuedCoupon coupon = issuedCouponRepository.findByIdAndUser_Id(couponId, userId)
                .orElseThrow(() -> new IllegalArgumentException("쿠폰을 찾을 수 없거나 접근 권한이 없습니다."));

        // 2. 이미 사용했거나 만료되었는지 확인
        if (coupon.getStatus() == IssuedCouponStatus.USED) {
            throw new IllegalStateException("이미 사용된 쿠폰입니다.");
        }
        if (coupon.getStatus() == IssuedCouponStatus.EXPIRED) {
            throw new IllegalStateException("만료된 쿠폰입니다.");
        }

        // 3. 가게 정보 가져오기 (PIN 확인용)
        Store store = coupon.getStore();

        // 4. PIN 번호 검증 (가게의 coupon_pin_hash와 비교)
        if (store.getCouponPinHash() == null || !passwordEncoder.matches(inputPin, store.getCouponPinHash())) {
            // 비밀번호 틀림 -> 에러 처리 (여기서 예외 던지면 GlobalExceptionHandler가 잡아서 실패 응답)
            throw new IllegalArgumentException("비밀번호가 올바르지 않습니다.");
        }

        // 5. 사용 처리 (Entity 내부 메서드 혹은 Setter 사용)
        coupon.use(); // 상태 변경 메서드 (아래 Entity 코드 참고)

        // 6. 응답 생성
        return CouponRedeemResponse.builder()
                .couponId(coupon.getId())
                .status(coupon.getStatus().name())
                .usedAt(coupon.getUsedAt().toString()) // 포맷팅 필요시 수정
                .storeId(store.getId())
                .build();
    }
}