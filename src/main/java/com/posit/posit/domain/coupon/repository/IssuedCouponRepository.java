package com.posit.posit.domain.coupon.repository;

import com.posit.posit.domain.coupon.entity.IssuedCoupon;
import com.posit.posit.domain.coupon.entity.IssuedCouponStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable; // 패키지 확인! (org.springframework...)
import java.util.List;
import java.util.Optional;

public interface IssuedCouponRepository extends JpaRepository<IssuedCoupon, Long> {

    // ==========================================
    // [사장님용] 내가 발행한 쿠폰 통계 및 조회
    // ==========================================
    @Query("SELECT count(c) FROM IssuedCoupon c WHERE c.template.createdBy.id = :userId")
    long countByUserId(@Param("userId") Long userId);

    @Query("SELECT count(c) FROM IssuedCoupon c WHERE c.template.createdBy.id = :userId AND c.status = :status")
    long countByUserIdAndStatus(@Param("userId") Long userId, @Param("status") IssuedCouponStatus status);

    @Query("SELECT c FROM IssuedCoupon c " +
            "JOIN FETCH c.user " +
            "JOIN FETCH c.template " +
            "WHERE c.template.createdBy.id = :userId " +
            "ORDER BY c.id DESC")
    List<IssuedCoupon> findAllByUserIdOrderByIdDesc(
            @Param("userId") Long userId,
            org.springframework.data.domain.Pageable pageable
    );

    @Query("SELECT c FROM IssuedCoupon c " +
            "JOIN FETCH c.user " +
            "JOIN FETCH c.template " +
            "WHERE c.template.createdBy.id = :userId AND c.id < :cursorId " +
            "ORDER BY c.id DESC")
    List<IssuedCoupon> findAllByUserIdAndIdLessThanOrderByIdDesc(
            @Param("userId") Long userId,
            @Param("cursorId") Long cursorId,
            org.springframework.data.domain.Pageable pageable
    );


    // ==========================================
    // [손님용] 내 쿠폰함 조회 (추가된 부분)
    // ==========================================

    // 1. 첫 페이지 조회 (커서 없음)
    @Query("SELECT c FROM IssuedCoupon c " +
            "JOIN FETCH c.template t " +          // 템플릿 정보 가져오기
            "JOIN FETCH t.createdBy.store " +     // ★ 가게 정보 한방에 가져오기 (User -> Store)
            "WHERE c.user.id = :userId AND c.status = :status " + // 내꺼 & 상태조건
            "ORDER BY c.id DESC")
    List<IssuedCoupon> findAllMyCouponsFirst(
            @Param("userId") Long userId,
            @Param("status") IssuedCouponStatus status,
            org.springframework.data.domain.Pageable pageable
    );

    // 2. 다음 페이지 조회 (커서 있음)
    @Query("SELECT c FROM IssuedCoupon c " +
            "JOIN FETCH c.template t " +
            "JOIN FETCH t.createdBy.store " +     // ★ 가게 정보 한방에 가져오기
            "WHERE c.user.id = :userId AND c.status = :status AND c.id < :cursorId " +
            "ORDER BY c.id DESC")
    List<IssuedCoupon> findAllMyCouponsNext(
            @Param("userId") Long userId,
            @Param("status") IssuedCouponStatus status,
            @Param("cursorId") Long cursorId,
            org.springframework.data.domain.Pageable pageable
    );

    @Query("SELECT c FROM IssuedCoupon c " +
            "JOIN FETCH c.template t " +
            "JOIN FETCH t.createdBy.store " + // 가게 ID 등을 위해 조인
            "WHERE c.id = :couponId AND c.user.id = :userId")
    Optional<IssuedCoupon> findByIdAndUserId(
            @Param("couponId") Long couponId,
            @Param("userId") Long userId
    );
}