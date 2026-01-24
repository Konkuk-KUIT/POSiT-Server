package com.posit.posit.domain.auth.dto.response;

import com.posit.posit.domain.user.entity.User;

public record LoginResponse(
        UserResponse user,
        TokenResponse tokens
) {
    public static LoginResponse of(User user, String accessToken, String refreshToken) {
        return new LoginResponse(UserResponse.of(user), TokenResponse.of(accessToken, refreshToken));
    }
}
