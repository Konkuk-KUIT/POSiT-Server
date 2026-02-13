package com.posit.posit.domain.auth.service;

import com.posit.posit.domain.auth.dto.request.*;
import com.posit.posit.domain.auth.dto.response.*;
import com.posit.posit.domain.auth.entity.AuthRefreshToken;
import com.posit.posit.domain.auth.entity.PhoneVerification;
import com.posit.posit.domain.auth.entity.PhoneVerificationStatus;
import com.posit.posit.domain.auth.repository.PhoneVerificationRepository;
import com.posit.posit.domain.auth.repository.RefreshTokenRepository;
import com.posit.posit.domain.store.entity.Store;
import com.posit.posit.domain.store.repository.StoreRepository;
import com.posit.posit.domain.user.entity.OwnerProfile;
import com.posit.posit.domain.user.entity.User;
import com.posit.posit.domain.user.entity.UserRole;
import com.posit.posit.domain.user.entity.UserStatus;
import com.posit.posit.domain.user.repository.OwnerProfileRepository;
import com.posit.posit.domain.user.repository.UserRepository;
import com.posit.posit.global.error.CustomException;
import com.posit.posit.global.error.ErrorCode;
import com.posit.posit.global.jwt.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static java.time.LocalDateTime.now;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final OwnerProfileRepository ownerProfileRepository;
    private final RefreshTokenRepository tokenRepository;
    private final PhoneVerificationRepository phoneVerificationRepository;

    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtProvider;
    private final StoreRepository storeRepository;

    @Transactional
    public SignupResponse signup(SignupRequest req) {
        // [수정 1] 휴대폰 인증 여부 확인 로직을 전부 주석 처리함 (무조건 통과)
        /*
        PhoneVerification pv = phoneVerificationRepository
                .findTopByPhoneOrderByCreatedAtDesc(req.phone())
                .orElseThrow(() -> new CustomException(ErrorCode.PHONE_VERIFICATION_NOT_FOUND));

        LocalDateTime now = now();

        // 만료된 인증은 무효 처리
        if (pv.isExpired(now)) {
            throw new CustomException(ErrorCode.PHONE_VERIFICATION_EXPIRED);
        }

        // 인증 완료(VERIFIED) 상태가 아니면 회원가입 불가
        if (!pv.isVerified()) {
            throw new CustomException(ErrorCode.PHONE_NOT_VERIFIED);
        }
        */

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
                req.role(),
                req.birth(),
                req.gender()
        );
        userRepository.save(user);

        // 4) OWNER면 owner_profile 생성 + store.owner_id 연결
        if (req.role() == UserRole.OWNER) {
            String businessNumber = req.ownerProfile().businessNumber();

            OwnerProfile owner = OwnerProfile.create(user, businessNumber);
            ownerProfileRepository.save(owner);

            // 가게가 없어도 에러 안 나게 처리 (있으면 연결)
            storeRepository.findByBusinessNumber(businessNumber)
                    .ifPresent(store -> store.assignOwner(user, req.ownerProfile().couponPin()));
        }

        // 5) 토큰 발급 + refreshToken 저장
        String accessToken = jwtProvider.generateAccessToken(user.getId(), user.getRole().name());
        String refreshToken = jwtProvider.generateRefreshToken(user.getId());

        String hashed = jwtProvider.hashRefreshToken(refreshToken);
        LocalDateTime exp = jwtProvider.refreshTokenExpiredAtFromNow();
        tokenRepository.save(AuthRefreshToken.issue(user, hashed, exp));

        return SignupResponse.of(user, accessToken, refreshToken);
    }

    @Transactional
    public LoginResponse login(@NotNull LoginRequest req) {
        User user = userRepository.findByLoginId(req.loginId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (user.getStatus() != null && user.getStatus().equals(UserStatus.INACTIVE)) {
            throw new IllegalStateException("비활성화된 유저");
        }
        if (!passwordEncoder.matches(req.password(), user.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }
        String accessToken = jwtProvider.generateAccessToken(user.getId(), user.getRole().name());
        String refreshToken = jwtProvider.generateRefreshToken(user.getId());

        String hashed = jwtProvider.hashRefreshToken(refreshToken);
        LocalDateTime exp = jwtProvider.refreshTokenExpiredAtFromNow();
        tokenRepository.save(AuthRefreshToken.issue(user, hashed, exp));
        return LoginResponse.of(user, accessToken, refreshToken);
    }

    @Transactional
    public TokenResponse rotate(String refreshTokenRaw) {
        Claims claims = jwtProvider.parseClaimsSafely(refreshTokenRaw)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_REFRESH_TOKEN));

        String tokenType = claims.get("tokenType", String.class);
        if (!"REFRESH".equals(tokenType)) {
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        Long userId = Long.valueOf(claims.getSubject());
        String tokenHash = jwtProvider.hashRefreshToken(refreshTokenRaw);
        LocalDateTime now = now();

        AuthRefreshToken current = tokenRepository
                .findByUserIdAndTokenHashAndRevokedAtIsNullAndExpiredAtAfter(userId, tokenHash, now)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_REFRESH_TOKEN));

        current.revoke();

        User user = current.getUser();

        String newAccess = jwtProvider.generateAccessToken(user.getId(), user.getRole().name());
        String newRefresh = jwtProvider.generateRefreshToken(user.getId());

        tokenRepository.save(AuthRefreshToken.issue(
                user,
                jwtProvider.hashRefreshToken(newRefresh),
                jwtProvider.refreshTokenExpiredAtFromNow()
        ));

        return TokenResponse.of(newAccess, newRefresh);
    }

    private static final int MAX_RESEND = 999999;
    private static final int MAX_ATTEMPT = 999999;

    private static final String DEMO_CODE = "123123";
    private static final String DEMO_HASH = "$2a$10$demodemocodedemodemoabcdef";

    @Transactional
    public PhoneVerificationResponse phoneVerify(PhoneVerificationRequest req) {
        LocalDateTime now = now();
        PhoneVerification pv = phoneVerificationRepository
                .findTopByPhoneOrderByCreatedAtDesc(req.phone())
                .orElseGet(() -> phoneVerificationRepository.save(
                        PhoneVerification.builder()
                                .phone(req.phone())
                                .codeHash(DEMO_HASH)
                                .expiredAt(now.plusMinutes(5))
                                .attemptCount(0)
                                .resendCount(0)
                                .status(PhoneVerificationStatus.PENDING)
                                .build()
                ));


        if (pv.getResendCount() != null && pv.getResendCount() >= MAX_RESEND) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }

        if (pv.isVerified()) {
            return PhoneVerificationResponse.from(pv);
        }

        if (pv.getResendCount() == null) {
            pv = PhoneVerification.builder()
                    .id(pv.getId())
                    .phone(pv.getPhone())
                    .codeHash(pv.getCodeHash())
                    .expiredAt(pv.getExpiredAt())
                    .verifiedAt(pv.getVerifiedAt())
                    .attemptCount(0)
                    .resendCount(0)
                    .createdAt(pv.getCreatedAt())
                    .status(pv.getStatus())
                    .build();
        }
        pv.increaseResend();
        pv.updateCodeHash(DEMO_HASH);
        pv.updateExpiredAt(now.plusMinutes(5));
        pv.markPending();

        return PhoneVerificationResponse.from(pv);
    }

    @Transactional
    public PhoneVerificationConfirmResponse confirm(PhoneVerificationConfirmRequest req) {
        PhoneVerification pv = phoneVerificationRepository
                .findById(req.verificationId())
                .orElseThrow(() -> new CustomException(ErrorCode.PHONE_VERIFICATION_NOT_FOUND));

        LocalDateTime now = now();

        // 번호가 달라도 일단 통과시켜줌 (개발 편의)
        // if (!pv.getPhone().equals(req.phone())) { throw ... }

        if (pv.isExpired(now)) {
            throw new CustomException(ErrorCode.PHONE_VERIFICATION_EXPIRED);
        }

        if (pv.isVerified()) {
            final PhoneVerification finalPv = pv;
            return userRepository.findByPhone(req.phone())
                    .map(user -> PhoneVerificationConfirmResponse.existing(finalPv, user.getId()))
                    .orElseGet(() -> PhoneVerificationConfirmResponse.newUser(finalPv, DEMO_CODE));
        }

        pv.increaseAttempt();

        // [수정 2] 특정 번호(DEMO_PHONE) 체크 로직 삭제 -> 모든 번호 허용
        /*
        if (!DEMO_PHONE.equals(req.phone())) {
            throw new CustomException(ErrorCode.PHONE_VERIFICATION_CODE_MISMATCH);
        }
        */

        pv.markVerified(now);

        final PhoneVerification finalPv = pv;

        return userRepository.findByPhone(req.phone())
                .map(user -> PhoneVerificationConfirmResponse.existing(finalPv, user.getId()))
                .orElseGet(() -> PhoneVerificationConfirmResponse.newUser(finalPv, DEMO_CODE));
    }

    @Transactional
    public void logout(LogoutRequest req) {
        String refreshToken = req.refreshToken();
        Claims claims = jwtProvider.parseClaimsSafely(refreshToken)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_REFRESH_TOKEN));

        String tokenType = claims.get("tokenType", String.class);
        if (!"REFRESH".equals(tokenType)) {
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        Long userId = Long.valueOf(claims.getSubject());
        String tokenHash = jwtProvider.hashRefreshToken(refreshToken);
        LocalDateTime now = LocalDateTime.now();

        AuthRefreshToken current = tokenRepository
                .findByUserIdAndTokenHashAndRevokedAtIsNullAndExpiredAtAfter(userId, tokenHash, now)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_REFRESH_TOKEN));

        current.revoke();
    }
}