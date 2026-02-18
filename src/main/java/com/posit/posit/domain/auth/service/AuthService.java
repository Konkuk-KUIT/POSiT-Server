package com.posit.posit.domain.auth.service;

import com.posit.posit.domain.auth.dto.request.*;
import com.posit.posit.domain.auth.dto.response.*;
import com.posit.posit.domain.auth.entity.AuthRefreshToken;
import com.posit.posit.domain.auth.entity.PhoneVerification;
import com.posit.posit.domain.auth.entity.PhoneVerificationStatus;
import com.posit.posit.domain.auth.repository.PhoneVerificationRepository;
import com.posit.posit.domain.auth.repository.RefreshTokenRepository;
import com.posit.posit.domain.coupon.entity.CouponTemplate;
import com.posit.posit.domain.coupon.repository.CouponTemplateRepository;
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
import com.posit.posit.global.sms.SmsService;
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
    private final CouponTemplateRepository couponTemplateRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtProvider;
    private final StoreRepository storeRepository;
    private final SmsService smsService;

    @Transactional
    public SignupResponse signup(SignupRequest req) {
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

        // signupToken 검증
        String signupToken = req.signupToken();
        if (signupToken == null || signupToken.isBlank()) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }
        // confirm 단계에서 발급된 signupToken 검증 (DB에는 hash로 저장)
        String savedHash = pv.getSignupTokenHash();
        if (savedHash == null || !passwordEncoder.matches(signupToken, savedHash)) {
            // 프로젝트에 INVALID_SIGNUP_TOKEN 같은 에러코드가 있다면 교체 권장
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }
        // 재사용 방지: 회원가입 성공 시 signupToken 즉시 소진
        pv.consumeSignupToken();
        phoneVerificationRepository.save(pv);
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
            // Owner 일 경우, 쿠폰 템플릿 3종 저장  (아메리카노, 디저트, 아이스티)
            createStandardTemplate(user);
        }

        // 5) 토큰 발급 + refreshToken 저장
        String accessToken = jwtProvider.generateAccessToken(user.getId(), user.getRole().name());
        String refreshToken = jwtProvider.generateRefreshToken(user.getId());

        String hashed = jwtProvider.hashRefreshToken(refreshToken);
        LocalDateTime exp = jwtProvider.refreshTokenExpiredAtFromNow();
        tokenRepository.save(AuthRefreshToken.issue(user, hashed, exp));

        return SignupResponse.of(user, accessToken, refreshToken);
    }

    private void createStandardTemplate(User owner) {
        final String AMERICANO = "https://posit-deploy.s3.ap-northeast-2.amazonaws.com/uploads/menu/AlexanderMenu1.jpeg";
        final String DESSERT = "https://posit-deploy.s3.ap-northeast-2.amazonaws.com/uploads/menu/LazyHourMenu3.jpeg";
        final String ICETEA = "https://posit-deploy.s3.ap-northeast-2.amazonaws.com/uploads/menu/ASMenu3.jpeg";

        if (!couponTemplateRepository.existsByCreatedByIdAndImage(owner.getId(), AMERICANO)) {
            couponTemplateRepository.save(
                    CouponTemplate.builder()
                            .title("아메리카노 1잔 무료 교환권")
                            .description("아메리카노 1잔 무료 제공")
                            .image(AMERICANO)
                            .validDays(30)
                            .createdBy(owner)
                            .build()
            );
        }

        if (!couponTemplateRepository.existsByCreatedByIdAndImage(owner.getId(), DESSERT)) {
            couponTemplateRepository.save(
                    CouponTemplate.builder()
                            .title("디저트 20% 할인 쿠폰")
                            .description("디저트 메뉴 20% 할인")
                            .image(DESSERT)
                            .validDays(30)
                            .createdBy(owner)
                            .build()
            );
        }

        if (!couponTemplateRepository.existsByCreatedByIdAndImage(owner.getId(), ICETEA)) {
            couponTemplateRepository.save(
                    CouponTemplate.builder()
                            .title("아이스티 1잔 무료 교환권")
                            .description("아이스티 1잔 무료 제공")
                            .image(ICETEA)
                            .validDays(30)
                            .createdBy(owner)
                            .build()
            );
        }
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

    private static final int MAX_RESEND = 3;
    private static final int MAX_ATTEMPT = 5;
    private static final int OTP_TTL_MINUTES = 5;
    private static final int OTP_LENGTH =6;

    private String generateOtp() {
        // 6자리 숫자
        int n = java.util.concurrent.ThreadLocalRandom.current().nextInt(0, 1_000_000);
        return String.format("%0" + OTP_LENGTH + "d", n);
    }

    @Transactional
    public PhoneVerificationResponse phoneVerify(PhoneVerificationRequest req) {
        LocalDateTime now = now();
        PhoneVerification pv = phoneVerificationRepository
                .findTopByPhoneOrderByCreatedAtDesc(req.phone())
                .orElse(null);

        if (pv != null && pv.isVerified() && !pv.isExpired(now)) {
            return PhoneVerificationResponse.from(pv);
        }

        if (pv == null || pv.isExpired(now)) {
            String otp = generateOtp();
            String codeHash = passwordEncoder.encode(otp);

            pv = phoneVerificationRepository.save(
                    PhoneVerification.builder()
                            .phone(req.phone())
                            .codeHash(codeHash)
                            .expiredAt(now.plusMinutes(OTP_TTL_MINUTES))
                            .attemptCount(0)
                            .resendCount(0)
                            .status(PhoneVerificationStatus.PENDING)
                            .build()
            );
            smsService.sendSms(req.phone(), otp);
            return PhoneVerificationResponse.from(pv);
        }

        Integer resend = (pv.getResendCount() == null) ? 0 : pv.getResendCount();
        if (resend >= MAX_RESEND) {
            throw new CustomException(ErrorCode.PHONE_VERIFICATION_RESEND_LIMIT);
        }
        String otp = generateOtp();
        pv.increaseResend();
        pv.updateCodeHash(passwordEncoder.encode(otp));
        pv.updateExpiredAt(now.plusMinutes(OTP_TTL_MINUTES));
        pv.markPending();
        phoneVerificationRepository.save(pv);
        smsService.sendSms(req.phone(), otp);

        return PhoneVerificationResponse.from(pv);
    }

    @Transactional(noRollbackFor = CustomException.class)
    public PhoneVerificationConfirmResponse confirm(PhoneVerificationConfirmRequest req) {
        PhoneVerification pv = phoneVerificationRepository.findById(req.verificationId())
                .orElseThrow(() -> new CustomException(ErrorCode.PHONE_VERIFICATION_NOT_FOUND));

        LocalDateTime now = now();
        String phone = req.phone();

        if (phone == null || !pv.getPhone().equals(phone)) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }

        if (pv.isExpired(now)) {
            throw new CustomException(ErrorCode.PHONE_VERIFICATION_EXPIRED);
        }

        if (pv.isVerified()) {
            final PhoneVerification finalPv = pv;
            return userRepository.findByPhone(phone)
                    .map(user -> PhoneVerificationConfirmResponse.existing(finalPv, user.getId()))
                    .orElseGet(() -> {
                        String raw = java.util.UUID.randomUUID().toString();
                        finalPv.issueSignupToken(passwordEncoder.encode(raw));
                        phoneVerificationRepository.save(finalPv);
                        return PhoneVerificationConfirmResponse.newUser(finalPv, raw);
                    });
        }
        Integer attempt = pv.getAttemptCount() == null ? 0 : pv.getAttemptCount();
        if (attempt >= MAX_ATTEMPT) {
            pv = PhoneVerification.builder()
                    .id(pv.getId())
                    .phone(pv.getPhone())
                    .codeHash(pv.getCodeHash())
                    .expiredAt(pv.getExpiredAt()).verifiedAt(pv.getVerifiedAt()).attemptCount(pv.getAttemptCount())
                    .resendCount(pv.getResendCount()).createdAt(pv.getCreatedAt()).status(PhoneVerificationStatus.LOCKED)
                    .build();
            phoneVerificationRepository.save(pv);
            throw new CustomException(ErrorCode.PHONE_VERIFICATION_ATTEMPT_LIMIT);
        }
        pv.increaseAttempt();
        boolean matches = passwordEncoder.matches(req.code(), pv.getCodeHash());
        if (!matches) {
            phoneVerificationRepository.save(pv);
            throw new CustomException(ErrorCode.PHONE_VERIFICATION_CODE_MISMATCH);
        }

        pv.markVerified(now);
        phoneVerificationRepository.save(pv);

        final PhoneVerification finalPv = pv;

        return userRepository.findByPhone(req.phone())
                .map(user -> PhoneVerificationConfirmResponse.existing(finalPv, user.getId()))
                .orElseGet(() -> {
                    String raw = java.util.UUID.randomUUID().toString();
                    finalPv.issueSignupToken(passwordEncoder.encode(raw));
                    phoneVerificationRepository.save(finalPv);
                    return PhoneVerificationConfirmResponse.newUser(finalPv, raw);
                });
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

    @Transactional(readOnly = true)
    public Boolean loginIdConfirm(String confirmId) {
        if (confirmId == null || confirmId.isBlank()) {
            throw new CustomException(ErrorCode.DTO_VALIDATION_FAILED);
        }

        // true  : 사용 가능 (중복 아님)
        // false : 이미 존재 (중복)
        return !userRepository.existsByLoginId(confirmId);
    }
}