package com.posit.posit.domain.user.controller;

import com.posit.posit.domain.user.dto.UserPrincipal;
import com.posit.posit.domain.user.dto.request.UserUpdateRequest;
import com.posit.posit.domain.user.dto.response.UserMyPageResponse;
import com.posit.posit.domain.user.service.UserService;
import com.posit.posit.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User API", description = "사용자(계정) 관련 API")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 내 계정 관리 (내 정보 조회)
    // GET /users/me
    @Operation(summary = "내 정보 조회", description = "현재 로그인한 사용자의 프로필 정보를 조회합니다.")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserMyPageResponse>> getMyPage(@AuthenticationPrincipal UserPrincipal user) {
        UserMyPageResponse response = userService.getMyPage(user.getId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // PATCH /users/me
    @Operation(summary = "내 정보 수정", description = "현재 로그인한 사용자의 프로필(닉네임, 프로필 이미지 등)을 수정합니다.")
    @PatchMapping("/me")
    public ResponseEntity<ApiResponse<UserMyPageResponse>> updateProfile(
                                                                          @AuthenticationPrincipal UserPrincipal user,
                                                                          @RequestBody @Valid UserUpdateRequest request
    ) {
        UserMyPageResponse response = userService.updateProfile(user.getId(), request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}