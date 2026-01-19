package com.posit.posit.domain.auth.service;

import com.posit.posit.domain.auth.dto.request.SignupRequest;
import com.posit.posit.domain.auth.dto.response.SignupResponse;
import com.posit.posit.domain.auth.repository.PhoneVerificationRepository;
import com.posit.posit.domain.auth.repository.RefreshTokenRepository;
import com.posit.posit.domain.user.repository.OwnerProfileRepository;
import com.posit.posit.domain.user.repository.UserRepository;
import com.posit.posit.global.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
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

    }

}
