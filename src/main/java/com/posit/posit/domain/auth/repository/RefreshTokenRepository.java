package com.posit.posit.domain.auth.repository;

import com.posit.posit.domain.auth.entity.AuthRefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<AuthRefreshToken, Long> {
    Optional<AuthRefreshToken> findTopByUserIdOrderByDesc(Long userId);

    Optional<AuthRefreshToken> findByValue(String value); // 리프레시 토큰 값으로 RefreshToken 엔티티 조회

    boolean existsByValue(String token); // 리프레시 토큰 값이 데이터베이스에 존재하는지 여부

    Optional<AuthRefreshToken> findByUserId(Long userId); // 사용자 ID로 RefreshToken 엔티티 조회
}
