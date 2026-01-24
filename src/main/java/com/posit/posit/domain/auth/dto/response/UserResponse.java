package com.posit.posit.domain.auth.dto.response;

import com.posit.posit.domain.user.entity.User;
import com.posit.posit.domain.user.entity.UserRole;

public record UserResponse(
        Long userId,
        UserRole role
) {
    public static UserResponse of(User user) {
        return new UserResponse(user.getId(), user.getRole());
    }
}
