package com.posit.posit.domain.coupon.repository;

import com.posit.posit.domain.coupon.entity.CouponTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CouponTemplateRepository extends JpaRepository<CouponTemplate, Long> {
    // 사장님(User)이 만든 템플릿 목록 조회
    List<CouponTemplate> findAllByCreatedBy_Id(Long userId);

    boolean existsByCreatedByIdAndImage(Long userId,String image);
}