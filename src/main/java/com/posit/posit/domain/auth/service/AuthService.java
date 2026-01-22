package com.posit.posit.domain.auth.service;

import com.posit.posit.domain.auth.dto.request.PhoneVerificationRequest;
import com.posit.posit.domain.auth.dto.request.SignupRequest;
import com.posit.posit.domain.auth.dto.response.PhoneVerificationResponse;
import com.posit.posit.domain.auth.dto.response.SignupResponse;
import com.posit.posit.domain.auth.dto.response.TokenResponse;
import com.posit.posit.domain.auth.entity.AuthRefreshToken;
import com.posit.posit.domain.auth.entity.PhoneVerification;
import com.posit.posit.domain.auth.repository.PhoneVerificationRepository;
import com.posit.posit.domain.auth.repository.RefreshTokenRepository;
import com.posit.posit.domain.user.entity.OwnerProfile;
import com.posit.posit.domain.user.entity.User;
import com.posit.posit.domain.user.entity.UserRole;
import com.posit.posit.domain.user.repository.OwnerProfileRepository;
import com.posit.posit.domain.user.repository.UserRepository;
import com.posit.posit.global.error.CustomException;
import com.posit.posit.global.error.ErrorCode;
import com.posit.posit.global.jwt.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final OwnerProfileRepository ownerProfileRepository;
    private final RefreshTokenRepository tokenRepository;
    private final PhoneVerificationRepository phoneVerificationRepository;

    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtProvider;

    @Transactional
    public SignupResponse signup(SignupRequest req) {
        // 1) 휴대폰 인증 확인 (가장 최근 요청 기준)
        PhoneVerification pv = phoneVerificationRepository
                .findTopByPhoneOrderByCreatedAtDesc(req.phone())
                .orElseThrow(() -> new CustomException(ErrorCode.PHONE_VERIFICATION_NOT_FOUND));

        LocalDateTime now = LocalDateTime.now();

        // 만료된 인증은 무효 처리
        if (pv.isExpired(now)) {
            throw new CustomException(ErrorCode.PHONE_VERIFICATION_EXPIRED);
        }

        // 인증 완료(VERIFIED) 상태가 아니면 회원가입 불가
        if (!pv.isVerified()) {
            throw new CustomException(ErrorCode.PHONE_NOT_VERIFIED);
        }

        // 2) 중복 체크
        if (userRepository.existsByLoginId(req.loginId())) {
            throw new CustomException(ErrorCode.DUPLICATE_LOGIN_ID);
        }
        if (userRepository.existsByPhone(req.phone())) {
            throw new CustomException(ErrorCode.DUPLICATE_PHONE);
        }

        // 3) 저장
        User user = User.create(
                req.loginId(),
                passwordEncoder.encode(req.password()),
                req.name(),
                req.phone(),
                req.role()
        );
        userRepository.save(user);

        // 4) OWNER면 owner_profile 생성 (예시)
        if (req.role() == UserRole.OWNER) {
            OwnerProfile owner = OwnerProfile.create(user, req.ownerProfile().businessNumber());
            ownerProfileRepository.save(owner);
        }

        // 5) 토큰 발급 + refreshToken 저장
        String accessToken = jwtProvider.generateAccessToken(user.getId(), user.getName());
        String refreshToken = jwtProvider.generateRefreshToken(user.getId());

        String hashed = jwtProvider.hashRefreshToken(refreshToken);
        LocalDateTime exp = jwtProvider.refreshTokenExpiredAtFromNow();
        tokenRepository.save(AuthRefreshToken.issue(user, hashed, exp));

        return SignupResponse.of(user, accessToken, refreshToken);
    }

    @Transactional
    public TokenResponse rotate(String refreshTokenRaw) {
        // 1) refreshToken JWT 자체 유효성(서명/만료) 검증 + Claims 추출
        Claims claims = jwtProvider.parseClaimsSafely(refreshTokenRaw)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_REFRESH_TOKEN));

        // 2) 토큰 타입 확인 (ACCESS 토큰으로 재발급 시도 방지)
        String tokenType = claims.get("tokenType", String.class);
        if (!"REFRESH".equals(tokenType)) {
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        Long userId = Long.valueOf(claims.getSubject());
        String tokenHash = jwtProvider.hashRefreshToken(refreshTokenRaw);
        LocalDateTime now = LocalDateTime.now();

        // 3) DB에 저장된 "현재 유효한(refresh, not revoked, not expired)" 토큰인지 확인
        AuthRefreshToken current = tokenRepository
                .findByUserIdAndTokenHashAndRevokedAtIsNullAndExpiredAtAfter(userId, tokenHash, now)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_REFRESH_TOKEN));

        // 4) 기존 토큰 revoke + 새 토큰 발급/저장 (rotation)
        current.revoke(); // dirty checking으로 revoked_at 업데이트

        User user = current.getUser(); // LAZY면 필요 시 fetch (동일 트랜잭션 내)

        String newAccess = jwtProvider.generateAccessToken(user.getId(), user.getName());
        String newRefresh = jwtProvider.generateRefreshToken(user.getId());

        tokenRepository.save(AuthRefreshToken.issue(
                user,
                jwtProvider.hashRefreshToken(newRefresh),
                jwtProvider.refreshTokenExpiredAtFromNow()
        ));

        return TokenResponse.of(newAccess, newRefresh);
    }

    private static final int MAX_RESEND = 5;
    private static final int MAX_ATTEMPT = 5;

    private static final String DEMO_CODE = "123123";

    @Transactional
    public PhoneVerificationResponse phoneVerify(PhoneVerificationRequest req) {
        PhoneVerification pv = phoneVerificationRepository
                .findTopByPhoneOrderByCreatedAtDesc(req.phone())
                .orElseThrow(() -> new CustomException(ErrorCode.PHONE_VERIFICATION_NOT_FOUND));
        LocalDateTime now = LocalDateTime.now();

        if (pv.getResendCount() != null && pv.getResendCount() >= MAX_RESEND) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }

        if(pv.isVerified()) {
            return PhoneVerificationResponse.from(pv);
        }
        pv.increaseResend();

        pv.updateCodeHash(passwordEncoder.encode(DEMO_CODE));

        pv.updateExpiredAt(now.plusMinutes(5));

        pv.markPending();

        return PhoneVerificationResponse.from(pv);
    }

    /**
     * 인증번호 확인 (데모: 000000 입력 시 통과)
     * - attemptCount 증가
     * - codeHash 비교 -> mismatch면 에러
     * - 성공하면 VERIFIED로 전환
     */

    public PhoneVerificationResponse confirm(String phone, String code) {
        PhoneVerification pv = phoneVerificationRepository
                .findTopByPhoneOrderByCreatedAtDesc(phone)
                .orElseThrow(() -> new CustomException(ErrorCode.PHONE_VERIFICATION_NOT_FOUND));

        LocalDateTime now = LocalDateTime.now();

        if (pv.isExpired(now)) {
            throw new CustomException(ErrorCode.PHONE_VERIFICATION_EXPIRED);
        }
        if (pv.getAttemptCount() != null && pv.getAttemptCount() >= MAX_ATTEMPT) {
            throw new CustomException(ErrorCode.PHONE_VERIFICATION_ATTEMPT_LIMIT);
        }

        // 이미 인증된 경우 idempotent 처리
        if (pv.isVerified()) {
            return PhoneVerificationResponse.from(pv);
        }

        pv.increaseAttempt();

        // 코드 비교 (BCrypt는 equals 비교가 아니라 matches 사용!)
        if (!passwordEncoder.matches(code, pv.getCodeHash())) {
            throw new CustomException(ErrorCode.PHONE_VERIFICATION_CODE_MISMATCH);
        }

        pv.markVerified(now);
        return PhoneVerificationResponse.from(pv);
    }
}
