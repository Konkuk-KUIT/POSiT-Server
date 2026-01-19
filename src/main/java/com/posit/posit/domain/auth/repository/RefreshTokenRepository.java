package com.posit.posit.domain.auth.repository;

import com.posit.posit.domain.auth.entity.AuthRefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<AuthRefreshToken, Long> {

    Optional<AuthRefreshToken> findTopByUserIdOrderByExpiredAtDesc(Long userId);

    Optional<AuthRefreshToken> findByTokenHash(String tokenHash);

    boolean existsByTokenHash(String tokenHash);

    Optional<AuthRefreshToken> findByUserId(Long userId);
}
