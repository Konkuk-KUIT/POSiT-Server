package com.posit.posit.domain.coupon.repository;

import com.posit.posit.domain.coupon.entity.IssuedCoupon;
import com.posit.posit.domain.coupon.entity.IssuedCouponStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IssuedCouponRepository extends JpaRepository<IssuedCoupon, Long> {

    // ==========================================
    // [사장님용]
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
            Pageable pageable
    );

    @Query("SELECT c FROM IssuedCoupon c " +
            "JOIN FETCH c.user " +
            "JOIN FETCH c.template " +
            "WHERE c.template.createdBy.id = :userId AND c.id < :cursorId " +
            "ORDER BY c.id DESC")
    List<IssuedCoupon> findAllByUserIdAndIdLessThanOrderByIdDesc(
            @Param("userId") Long userId,
            @Param("cursorId") Long cursorId,
            Pageable pageable
    );


    // ==========================================
    // [손님용] 내 쿠폰함 조회
    // ==========================================

    /**
     * 1. 첫 페이지 조회 (cursorId 없음)
     * 구조: 쿠폰 -> 템플릿 -> 생성자(사장님) -> 가게
     * 설명: 모든 연관 관계를 Fetch Join으로 한 방에 가져옵니다.
     */
    @Query("SELECT c FROM IssuedCoupon c " +
            "JOIN FETCH c.template t " +
            "JOIN FETCH t.createdBy u " +  // 템플릿 만든 사장님(User)
            "JOIN FETCH u.store s " +      // 사장님의 가게(Store)
            "WHERE c.user.id = :userId AND c.status = :status " +
            "ORDER BY c.id DESC")
    List<IssuedCoupon> findAllMyCouponsFirst(
            @Param("userId") Long userId,
            @Param("status") IssuedCouponStatus status,
            Pageable pageable
    );

    /**
     * 2. 다음 페이지 조회 (cursorId 있음)
     * 설명: 위와 같고 WHERE 절에 커서 조건(c.id < :cursorId)만 추가됨
     */
    @Query("SELECT c FROM IssuedCoupon c " +
            "JOIN FETCH c.template t " +
            "JOIN FETCH t.createdBy u " +
            "JOIN FETCH u.store s " +
            "WHERE c.user.id = :userId AND c.status = :status AND c.id < :cursorId " +
            "ORDER BY c.id DESC")
    List<IssuedCoupon> findAllMyCouponsNext(
            @Param("userId") Long userId,
            @Param("status") IssuedCouponStatus status,
            @Param("cursorId") Long cursorId,
            Pageable pageable
    );

    /**
     * 3. 쿠폰 상세 조회 및 사용 (Redeem)
     * 설명: 가게 비밀번호(PIN) 확인 등을 위해 Store 정보까지 한 번에 가져옵니다.
     */
    @Query("SELECT c FROM IssuedCoupon c " +
            "JOIN FETCH c.template t " +
            "JOIN FETCH t.createdBy u " +
            "JOIN FETCH u.store s " +
            "WHERE c.id = :couponId AND c.user.id = :userId")
    Optional<IssuedCoupon> findByIdAndUserId(
            @Param("couponId") Long couponId,
            @Param("userId") Long userId
    );

    Optional<IssuedCoupon> findByIdAndUser_Id(Long id, Long userId);
}