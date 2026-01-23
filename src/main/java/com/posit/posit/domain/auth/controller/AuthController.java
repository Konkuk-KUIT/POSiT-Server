package com.posit.posit.domain.auth.controller;

import com.posit.posit.domain.auth.dto.request.PhoneVerificationConfirmRequest;
import com.posit.posit.domain.auth.dto.request.PhoneVerificationRequest;
import com.posit.posit.domain.auth.dto.request.SignupRequest;
import com.posit.posit.domain.auth.dto.response.PhoneVerificationConfirmResponse;
import com.posit.posit.domain.auth.dto.response.PhoneVerificationResponse;
import com.posit.posit.domain.auth.dto.response.SignupResponse;
import com.posit.posit.domain.auth.service.AuthService;
import com.posit.posit.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signup")
    public ApiResponse<SignupResponse> signup(@Valid @RequestBody SignupRequest request) {
        return ApiResponse.success(authService.signup(request));
    }

    @PostMapping("/phone/verification")
    public ApiResponse<PhoneVerificationResponse> phoneVerify(@Valid @RequestBody PhoneVerificationRequest request) {
        return ApiResponse.success(authService.phoneVerify(request));
    }

    @PostMapping("/phone/verification/confirm")
    public ApiResponse<PhoneVerificationConfirmResponse> phoneConfirm(@RequestBody PhoneVerificationConfirmRequest request) {
        return ApiResponse.success(authService.confirm(request));
    }
}
