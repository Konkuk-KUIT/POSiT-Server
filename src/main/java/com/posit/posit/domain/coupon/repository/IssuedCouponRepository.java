package com.posit.posit.domain.coupon.repository;

import com.posit.posit.domain.coupon.entity.IssuedCoupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IssuedCouponRepository extends JpaRepository<IssuedCoupon, Long> {
    //특정 가게에서 발급된 쿠폰 총 개수 세기
    long countByStoreId(Long storeId);

}