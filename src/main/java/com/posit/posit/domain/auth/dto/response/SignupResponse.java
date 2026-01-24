package com.posit.posit.domain.auth.dto.response;

import com.posit.posit.domain.user.entity.User;

public record SignupResponse(
        UserResponse user,
        TokenResponse tokens
) {
    public static SignupResponse of(User user, String accessToken, String refreshToken) {
        return new SignupResponse(UserResponse.of(user), TokenResponse.of(accessToken, refreshToken));
    }
}
