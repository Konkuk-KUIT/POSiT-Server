package com.posit.posit.domain.auth.service;

import com.posit.posit.domain.auth.dto.request.SignupRequest;
import com.posit.posit.domain.auth.dto.response.SignupResponse;
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
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final OwnerProfileRepository ownerProfileRepository;
    private final RefreshTokenRepository tokenRepository;
    private final PhoneVerificationRepository phoneVerificationRepository;

    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtProvider;
    private final RefreshTokenHashser refreshTokenHashser;

    @Transactional
    public SignupResponse signup(SignupRequest req) {

        // 1) 휴대폰 인증 확인
        PhoneVerification pv = phoneVerificationRepository
                .findTopByPhoneOrderByCreatedAtDesc(req.phone())
                .orElseThrow(() -> new CustomException(ErrorCode.PHONE_VERIFICATION_NOT_FOUND));

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
            OwnerProfile owner = OwnerProfile.create(user.getId(), req.ownerProfile().businessNumber());
            ownerProfileRepository.save(owner);
        }

        // 5) 토큰 발급 + refreshToken 저장
        String accessToken = jwtProvider.generateAccessToken(user.getId(), user.getName());
        String refreshToken = jwtProvider.generateRefreshToken(user.getId());

        String hashed = refreshTokenHashser.hash(refreshToken);
        tokenRepository.save(AuthRefreshToken.issue(user.getId(), hashed));

        return SignupResponse.of(user.getId(), user.getRole(), accessToken, refreshToken);
    }

}
