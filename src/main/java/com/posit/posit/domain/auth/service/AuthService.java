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
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;

import static java.time.LocalDateTime.*;

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
        // 1) 휴대폰 인증 확인 (가장 최근 요청 기준)
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

            Store store = storeRepository.findByBusinessNumber(businessNumber)
                    .orElseThrow(() -> new CustomException(ErrorCode.STORE_NOT_FOUND));
            store.assignOwner(user, req.ownerProfile().couponPin());
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
    public LoginResponse login(@NotNull LoginRequest req) {
        User user = userRepository.findByLoginId(req.loginId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (user.getStatus() != null && user.getStatus().equals(UserStatus.INACTIVE)) {
            throw new IllegalStateException("비활성화된 유저");
        }
        if (!passwordEncoder.matches(req.password(), user.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }
        // 5) 토큰 발급 + refreshToken 저장
        String accessToken = jwtProvider.generateAccessToken(user.getId(), user.getName());
        String refreshToken = jwtProvider.generateRefreshToken(user.getId());

        String hashed = jwtProvider.hashRefreshToken(refreshToken);
        LocalDateTime exp = jwtProvider.refreshTokenExpiredAtFromNow();
        tokenRepository.save(AuthRefreshToken.issue(user, hashed, exp));
        return LoginResponse.of(user, accessToken, refreshToken);
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
        LocalDateTime now = now();

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

    private static final int MAX_RESEND = 999999;
    private static final int MAX_ATTEMPT = 999999;

    private static final String DEMO_CODE = "123123";
    private static final String DEMO_PHONE = "01012345678";
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

        // 이미 인증된 경우
        if (pv.isVerified()) {
            return PhoneVerificationResponse.from(pv);
        }

        // resend 카운트 증가 (null-safe)
        if (pv.getResendCount() == null) {
            // legacy 데이터 대비
            pv = PhoneVerification.builder()
                    .id(pv.getId())
                    .phone(pv.getPhone())
                    .codeHash(pv.getCodeHash())
                    .expiredAt(pv.getExpiredAt())
                    .verifiedAt(pv.getVerifiedAt())
                    .attemptCount(pv.getAttemptCount() == null ? 0 : pv.getAttemptCount())
                    .resendCount(0)
                    .createdAt(pv.getCreatedAt())
                    .status(pv.getStatus())
                    .build();
        }
        pv.increaseResend();

        // 데모: SMS 미발송이므로 codeHash는 고정값 유지(혹은 갱신해도 됨)
        pv.updateCodeHash(DEMO_HASH);

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
    @Transactional
    public PhoneVerificationConfirmResponse confirm(PhoneVerificationConfirmRequest req) {
        PhoneVerification pv = phoneVerificationRepository
                .findById(req.verificationId())
                .orElseThrow(() -> new CustomException(ErrorCode.PHONE_VERIFICATION_NOT_FOUND));

        LocalDateTime now = now();

        // verificationId를 다른 번호로 재사용하는 위변조 방지
        if (!pv.getPhone().equals(req.phone())) {
            throw new CustomException(ErrorCode.PHONE_VERIFICATION_NOT_FOUND);
        }

        if (pv.isExpired(now)) {
            throw new CustomException(ErrorCode.PHONE_VERIFICATION_EXPIRED);
        }

        Integer attempt = pv.getAttemptCount();
        if (attempt != null && attempt >= MAX_ATTEMPT) {
            throw new CustomException(ErrorCode.PHONE_VERIFICATION_ATTEMPT_LIMIT);
        }
        // 이미 인증된 경우 idempotent 처리
        if (pv.isVerified()) {
            final PhoneVerification finalPv = pv;
            return userRepository.findByPhone(req.phone())
                    .map(user -> PhoneVerificationConfirmResponse.existing(finalPv, user.getId()))
                    .orElseGet(() -> PhoneVerificationConfirmResponse.newUser(finalPv, DEMO_CODE));
        }

        // attempt 증가 (null-safe)
        if (pv.getAttemptCount() == null) {
            // legacy 데이터 대비
            pv = PhoneVerification.builder()
                    .id(pv.getId())
                    .phone(pv.getPhone())
                    .codeHash(pv.getCodeHash())
                    .expiredAt(pv.getExpiredAt())
                    .verifiedAt(pv.getVerifiedAt())
                    .attemptCount(0)
                    .resendCount(pv.getResendCount() == null ? 0 : pv.getResendCount())
                    .createdAt(pv.getCreatedAt())
                    .status(pv.getStatus())
                    .build();
        }
        pv.increaseAttempt();

        // 데모 정책: 화이트리스트 번호만 통과
        // (데모데이 전까지는 SMS 발송이 없으니 code는 형식만 받고, 통과 조건은 phone만 본다)
        if (!DEMO_PHONE.equals(req.phone())) {
            throw new CustomException(ErrorCode.PHONE_VERIFICATION_CODE_MISMATCH);
        }

        pv.markVerified(now);

        final PhoneVerification finalPv = pv;

        // 기존 회원 검증 후 응답 분기처리
        return userRepository.findByPhone(req.phone())
                .map(user -> PhoneVerificationConfirmResponse.existing(finalPv, user.getId()))
                .orElseGet(() -> PhoneVerificationConfirmResponse.newUser(finalPv, DEMO_CODE));
    }

    @Transactional
    public void logout(LogoutRequest req) {
        String refreshToken = req.refreshToken();
        // 1) refreshToken JWT 자체 유효성(서명/만료) 검증 + Claims 추출
        Claims claims = jwtProvider.parseClaimsSafely(refreshToken)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_REFRESH_TOKEN));

        // 2) 토큰 타입 확인 (ACCESS로 로그아웃 시도 방지)
        String tokenType = claims.get("tokenType", String.class);
        if (!"REFRESH".equals(tokenType)) {
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        Long userId = Long.valueOf(claims.getSubject());
        String tokenHash = jwtProvider.hashRefreshToken(refreshToken);
        LocalDateTime now = LocalDateTime.now();

        // 3) DB에 저장된 현재 유효한 refresh 토큰인지 확인 후 revoke
        AuthRefreshToken current = tokenRepository
                .findByUserIdAndTokenHashAndRevokedAtIsNullAndExpiredAtAfter(userId, tokenHash, now)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_REFRESH_TOKEN));

        // 4) revoke 처리 (dirty checking으로 revoked_at 업데이트)
        current.revoke();
    }
}