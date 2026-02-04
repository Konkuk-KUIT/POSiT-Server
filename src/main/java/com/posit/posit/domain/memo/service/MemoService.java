package com.posit.posit.domain.memo.service;

import com.posit.posit.domain.concern.entity.Concern;
import com.posit.posit.domain.memo.dto.request.MemoCreateRequest;
import com.posit.posit.domain.memo.dto.request.MemoUpdateRequest;
import com.posit.posit.domain.memo.dto.response.MemoCreateResponse;
import com.posit.posit.domain.memo.dto.response.MemoUpdateResponse;
import com.posit.posit.domain.memo.entity.Memo;
import com.posit.posit.domain.memo.entity.MemoStatus;
import com.posit.posit.domain.memo.entity.MemoType;
import com.posit.posit.domain.store.entity.Store;
import com.posit.posit.domain.store.repository.MemoRepository;
import com.posit.posit.domain.store.repository.StoreRepository;
import com.posit.posit.domain.user.entity.User;
import com.posit.posit.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MemoService {

    private final MemoRepository memoRepository;
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;
    private final com.posit.posit.domain.store.repository.ConcernRepository concernRepository;

    public MemoCreateResponse createMemo(Long userId, Long storeId, MemoCreateRequest request) {

        // 1. 유저 & 가게 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 가게입니다."));

        // 2. 타입별 검증 및 Concern 설정
        Concern concern = null;

        if (request.getMemoType() == MemoType.ANSWER) {
            // [ANSWER] concernId 필수 검증
            if (request.getConcernId() == null) {
                throw new IllegalArgumentException("답변(ANSWER) 작성 시 고민 ID(concernId)는 필수입니다.");
            }
            // 고민 조회 및 가게 일치 여부 확인
            concern = concernRepository.findById(request.getConcernId())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 고민글입니다."));

            if (!concern.getStore().getId().equals(storeId)) {
                throw new IllegalArgumentException("해당 고민은 이 가게의 고민이 아닙니다.");
            }
        } else if (request.getMemoType() == MemoType.FREE) {
            // [FREE] freeType 필수 검증
            if (request.getFreeType() == null) {
                throw new IllegalArgumentException("자유 메모(FREE) 작성 시 카테고리(freeType)는 필수입니다.");
            }
        }

        // 3. 이미지 처리 (List -> Comma Separated String)
        // DB에 image 컬럼이 하나이므로, 여러 장일 경우 콤마로 잇거나 첫 번째만 저장
        String imageString = null;
        if (request.getImages() != null && !request.getImages().isEmpty()) {
            // 예: "key1,key2,key3" 형태로 변환
            imageString = request.getImages().stream()
                    .map(MemoCreateRequest.ImageDto::getImageKey)
                    .collect(Collectors.joining(","));
        }

        // 4. 메모 엔티티 생성 및 저장
        Memo memo = Memo.builder()
                .store(store)
                .user(user)
                .memoType(request.getMemoType())
                .concern(concern) // ANSWER면 값 있음, FREE면 null
                .freeType(request.getFreeType()) // FREE면 값 있음, ANSWER면 null
                .title(request.getTitle())
                .content(request.getContent())
                .image(imageString)
                .status(MemoStatus.REVIEWING) // 기본값
                .build();

        memoRepository.save(memo);

        // 5. 응답 반환
        return MemoCreateResponse.from(memo);
    }

    // 2. 메모 수정
    @Transactional
    public MemoUpdateResponse updateMemo(Long userId, Long memoId, MemoUpdateRequest request) {

        // 1. 메모 조회
        Memo memo = memoRepository.findById(memoId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메모입니다."));

        // 2. 권한 검증 (작성자 본인인지?)
        if (!memo.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("메모를 수정할 권한이 없습니다.");
        }

        // 3. 수정 (Entity 메서드 호출)
        memo.update(
                request.getTitle(),
                request.getContent(),
                request.getImageUrl(),
                request.getFreeType()
        );

        // 4. 응답 반환 (Transactional 덕분에 자동 저장됨)
        return MemoUpdateResponse.from(memo);
    }
}