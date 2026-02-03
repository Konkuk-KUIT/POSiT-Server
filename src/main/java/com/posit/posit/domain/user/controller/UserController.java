package com.posit.posit.domain.user.controller;

import com.posit.posit.domain.user.dto.UserPrincipal;
import com.posit.posit.domain.user.dto.request.UserUpdateRequest;
import com.posit.posit.domain.user.dto.response.UserMyPageResponse;
import com.posit.posit.domain.user.service.UserService;
import com.posit.posit.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 내 계정 관리 (내 정보 조회)
    // GET /users/me
    @GetMapping("/me")
    public ResponseEntity<?> getMyPage(@AuthenticationPrincipal UserPrincipal user) {
        UserMyPageResponse response = userService.getMyPage(user.getId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // PATCH /users/me
    @PatchMapping("/me")
    public ResponseEntity<?> updateProfile(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestBody @Valid UserUpdateRequest request
    ) {
        UserMyPageResponse response = userService.updateProfile(user.getId(), request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}