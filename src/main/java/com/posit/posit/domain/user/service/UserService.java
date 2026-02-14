package com.posit.posit.domain.user.service;

import com.posit.posit.domain.concern.entity.Concern;
import com.posit.posit.domain.store.dto.response.OwnerConcernListResponse;
import com.posit.posit.domain.store.repository.ConcernRepository;
import com.posit.posit.domain.user.dto.request.UserUpdateRequest;
import com.posit.posit.domain.user.dto.response.UserMyPageResponse;
import com.posit.posit.domain.user.entity.User;
import com.posit.posit.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.updatePassword(passwordEncoder.encode(request.getPassword()));
        }

        // 3. 수정된 정보 반환 (확인용)
        return UserMyPageResponse.from(user);
    }

    ConcernRepository concernRepository;

    //게스트 기준 메모들 조회
    public OwnerConcernListResponse getStoreConcerns(Long storeId, Long cursorId, int size) {

        // 1. Repository 호출 (storeId 기준 조회)
        Slice<Object[]> slice = concernRepository.findStoreConcernsWithCount(
                storeId,
                cursorId,
                PageRequest.of(0, size)
        );

        // 2. DTO 변환 (기존 로직과 동일)
        List<OwnerConcernListResponse.ConcernItem> items = slice.getContent().stream()
                .map(row -> {
                    Concern concern = (Concern) row[0];
                    Long count = (Long) row[1];
                    // DTO의 from 메서드 재사용 (제목 자르기 로직 등)
                    return OwnerConcernListResponse.ConcernItem.from(concern, count);
                })
                .collect(Collectors.toList());

        // 3. 커서 계산
        Long nextCursor = null;
        if (slice.hasNext() && !items.isEmpty()) {
            nextCursor = items.get(items.size() - 1).getConcernId();
        }

        return OwnerConcernListResponse.builder()
                .concerns(items)
                .nextCursorId(nextCursor)
                .build();
    }
}