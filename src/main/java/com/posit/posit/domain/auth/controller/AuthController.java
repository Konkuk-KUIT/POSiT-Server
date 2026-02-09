package com.posit.posit.domain.auth.controller;

import com.posit.posit.domain.auth.dto.request.*;
import com.posit.posit.domain.auth.dto.response.LoginResponse;
import com.posit.posit.domain.auth.dto.response.PhoneVerificationConfirmResponse;
import com.posit.posit.domain.auth.dto.response.PhoneVerificationResponse;
import com.posit.posit.domain.auth.dto.response.SignupResponse;
import com.posit.posit.domain.auth.service.AuthService;
import com.posit.posit.global.response.ApiResponse;
import com.posit.posit.global.swagger.ApiErrorCodes;
import com.posit.posit.global.swagger.SwaggerErrorSet;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "Auth", description = "인증/인가 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    @Operation(summary = "회원가입", description = "휴대폰 인증 기반 회원가입 진행")
    @ApiErrorCodes(SwaggerErrorSet.AUTH_SIGNUP)
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<SignupResponse>> signup(@Valid @RequestBody SignupRequest request) {
        return ResponseEntity.ok(ApiResponse.success(authService.signup(request)));
    }

    @Operation(summary = "휴대폰 인증번호 요청", description = "회원가입 시 휴대폰 인증번호 요청(발급)")
    @ApiErrorCodes(SwaggerErrorSet.PHONE_VERIFY_REQUEST)
    @PostMapping("/phone/verification")
    public ResponseEntity<ApiResponse<PhoneVerificationResponse>>phoneVerify(@Valid @RequestBody PhoneVerificationRequest request) {
        return ResponseEntity.ok(ApiResponse.success(authService.phoneVerify(request)));
    }

    @Operation(summary = "휴대폰 인증번호 확인", description = "회원가입 시 입력한 인증번호를 확인하고 회원가입 분기 처리")
    @ApiErrorCodes(SwaggerErrorSet.PHONE_VERIFY_CONFIRM)
    @PostMapping("/phone/verification/confirm")
    public ResponseEntity<ApiResponse<PhoneVerificationConfirmResponse>> phoneConfirm(@RequestBody PhoneVerificationConfirmRequest request) {
        return ResponseEntity.ok(ApiResponse.success(authService.confirm(request)));
    }

    @Operation(summary = "로그아웃", description = "RefreshToken을 무효화하고 로그아웃 처리")
    @ApiErrorCodes(SwaggerErrorSet.AUTH_LOGOUT)
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@RequestBody LogoutRequest request) {
        authService.logout(request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(summary = "로그인", description = "아이디/비밀번호로 로그인하고 토큰을 발급합니다.")
    @ApiErrorCodes(SwaggerErrorSet.AUTH_LOGIN)
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(ApiResponse.success(authService.login(request)));
    }
}
