package com.posit.posit.domain.user.repository;

import com.posit.posit.domain.user.entity.OwnerProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OwnerProfileRepository extends JpaRepository<OwnerProfile, Long> {
    boolean existsByBusinessNumber(String businessNumber);
    // 사장님 ID(userId)로 프로필(사업자번호 포함) 조회
    Optional<OwnerProfile> findByUserId(Long userId);
}

