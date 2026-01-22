package com.posit.posit.domain.auth.repository;

import com.posit.posit.domain.auth.entity.AuthRefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<AuthRefreshToken, Long> {

    Optional<AuthRefreshToken> findTopByUserIdOrderByExpiredAtDesc(Long userId);

    Optional<AuthRefreshToken> findByTokenHash(String tokenHash);

    boolean existsByTokenHash(String tokenHash);

    Optional<AuthRefreshToken> findByUserId(Long userId);

    Optional<AuthRefreshToken> findByUserIdAndTokenHashAndRevokedAtIsNullAndExpiredAtAfter(
            Long userId, String tokenHash, LocalDateTime now
    );
}
