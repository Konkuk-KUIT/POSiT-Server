package com.posit.posit.domain.auth.service;

import com.posit.posit.domain.auth.entity.AuthRefreshToken;
import com.posit.posit.domain.auth.repository.RefreshTokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    // 리프레시 토큰 추가
    @Transactional
    public AuthRefreshToken addRefreshToken(AuthRefreshToken refreshToken) {
        return refreshTokenRepository.save(refreshToken);
    }

    // 리프레시 토큰 조회
    @Transactional
    public Optional<AuthRefreshToken> findRefreshToken(String refreshToken) {
        return refreshTokenRepository.findByTokenHash(refreshToken);
    }

    // 리프레시 토큰 삭제
    public void deleteRefreshToken(String refreshToken) {
        refreshTokenRepository.findByTokenHash(refreshToken).ifPresent(refreshTokenRepository::delete);
    }

    // 사용자 기반 리프레시 토큰 삭제
    public void deleteRefreshToken(Long userId) {
        refreshTokenRepository.findByUserId(userId).ifPresent(refreshTokenRepository::delete);
    }

    // 리프레시 토큰 유효성 검증
    public boolean isRefreshTokenValid(String refreshToken) {
        return refreshTokenRepository.existsByTokenHash(refreshToken);
    }
}