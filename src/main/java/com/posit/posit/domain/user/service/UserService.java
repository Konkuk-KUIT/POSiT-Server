package com.posit.posit.domain.user.service;

import com.posit.posit.domain.user.dto.request.UserUpdateRequest;
import com.posit.posit.domain.user.dto.response.UserMyPageResponse;
import com.posit.posit.domain.user.entity.User;
import com.posit.posit.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    // 내 정보 조회
    public UserMyPageResponse getMyPage(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        return UserMyPageResponse.from(user);
    }

    @Transactional
    public UserMyPageResponse updateProfile(Long userId, UserUpdateRequest request) {

        // 1. 유저 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 2. 정보 업데이트 (Dirty Checking)
        user.updateProfile(
                request.getName(),
                request.getPhone(),
                request.getBirthDate(),
                request.getGender()
        );

        // 3. 수정된 정보 반환 (확인용)
        return UserMyPageResponse.from(user);
    }
}