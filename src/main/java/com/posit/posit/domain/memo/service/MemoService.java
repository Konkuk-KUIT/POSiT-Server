package com.posit.posit.domain.memo.service;

import com.posit.posit.domain.concern.entity.Concern;
import com.posit.posit.domain.memo.dto.request.MemoCreateRequest;
import com.posit.posit.domain.memo.dto.request.MemoUpdateRequest;
import com.posit.posit.domain.memo.dto.response.MemoCreateResponse;
import com.posit.posit.domain.memo.dto.response.MemoUpdateResponse;
import com.posit.posit.domain.memo.dto.response.MyMemoDetailResponse;
import com.posit.posit.domain.memo.dto.response.MyMemoListResponse;
import com.posit.posit.domain.memo.entity.*;
import com.posit.posit.domain.memo.repository.DecisionRepository;
import com.posit.posit.domain.store.entity.Store;
import com.posit.posit.domain.memo.repository.MemoRepository;
import com.posit.posit.domain.store.repository.StoreRepository;
import com.posit.posit.domain.user.entity.User;
import com.posit.posit.domain.user.repository.UserRepository;
import com.posit.posit.global.error.CustomException;
import com.posit.posit.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MemoService {

    private final MemoRepository memoRepository;
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;
    private final com.posit.posit.domain.store.repository.ConcernRepository concernRepository;

    @Transactional
    public MemoCreateResponse createMemo(Long userId, Long storeId, MemoCreateRequest request) {

        // 1. 유저 & 가게 조회 (기존 동일)
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new CustomException(ErrorCode.STORE_NOT_FOUND));

        // 2. 타입별 검증 및 Concern 설정 (기존 동일)
        Concern concern = null;

        if (request.getMemoType() == MemoType.ANSWER) {
            if (request.getConcernId() == null) {
                throw new CustomException(ErrorCode.ANSWER_CONCERN_ESSENTIAL);
            }
            concern = concernRepository.findById(request.getConcernId())
                    .orElseThrow(() -> new CustomException(ErrorCode.CONCERN_NOT_FOUND));

            if (!concern.getStore().getId().equals(storeId)) {
                throw new CustomException(ErrorCode.CONCERN_STORE_MISMATCH);
            }
        } else if (request.getMemoType() == MemoType.FREE) {
            if (request.getFreeType() == null) {
                throw new CustomException(ErrorCode.FREE_TYPE_ESSENTIAL);
            }
        }

        // 3. 메모 엔티티 생성 (이미지는 아직 비어있음)
        Memo memo = Memo.builder()
                .store(store)
                .user(user)
                .memoType(request.getMemoType())
                .concern(concern)
                .freeType(request.getFreeType())
                .title(request.getTitle())
                .content(request.getContent())
                .status(MemoStatus.REVIEWING)
                .ownerRead(false)
                .build();

        // 4. [수정됨] 이미지 처리 (DTO List -> Entity List)
        if (request.getImages() != null && !request.getImages().isEmpty()) {
            List<MemoImage> memoImages = request.getImages().stream()
                    .map(imageDto -> MemoImage.builder()
                            .imageUrl(imageDto.getImageKey()) // DTO에서 Key 꺼냄
                            .memo(memo) // ★ 중요: 양방향 연관관계 (자식 -> 부모)
                            .build())
                    .collect(Collectors.toList());

            // 부모 엔티티에 자식 리스트 추가 (부모 -> 자식)
            // Memo 엔티티에 cascade = CascadeType.ALL이 걸려있어서, memo만 저장해도 이미지가 같이 저장됨
            memo.getImages().addAll(memoImages);
        }

        // 5. 저장 (Memo + MemoImage들이 한 번에 저장됨)
        memoRepository.save(memo);

        // 6. 응답 반환
        return MemoCreateResponse.from(memo);
    }
    // 2. 메모 수정
    @Transactional
    public MemoUpdateResponse updateMemo(Long userId, Long memoId, MemoUpdateRequest request) {

        // 1. 메모 조회
        Memo memo = memoRepository.findById(memoId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메모입니다."));

        // 2. 권한 검증
        if (!memo.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("메모를 수정할 권한이 없습니다.");
        }

        // 3. 상태 검증 (채택/거절된 건 수정 불가)
        if (memo.getStatus() == MemoStatus.ADOPTED || memo.getStatus() == MemoStatus.REJECTED) {
            throw new IllegalStateException("이미 처리된(거절/채택) 메모는 수정할 수 없습니다.");
        }

        // 4. 텍스트 정보 수정 (Entity 내부 메서드 활용)
        // (Entity의 update 메서드 내부에서 null 체크를 하고 있으므로 그대로 넘겨도 됨)
        memo.update(
                request.getTitle(),
                request.getContent(),
                request.getFreeType()
        );

        // 5. [핵심] 이미지 리스트 수정 (갈아끼우기 전략)
        // 요청에 imageKeys가 null이면 "이미지 수정 안 함(유지)"으로 간주
        // 빈 리스트([])가 오면 "이미지 모두 삭제"로 간주
        if (request.getImageKeys() != null) {

            // 5-1. 기존 이미지 삭제 (orphanRemoval 덕분에 DB에서도 DELETE 쿼리 나감)
            memo.getImages().clear();

            // 5-2. 새 이미지 리스트 생성
            List<MemoImage> newImages = request.getImageKeys().stream()
                    .map(key -> MemoImage.builder()
                            .imageUrl(key)
                            .memo(memo) // 연관관계 설정
                            .build())
                    .collect(Collectors.toList());

            // 5-3. 새 이미지 등록
            memo.getImages().addAll(newImages);
        }

        // 6. 응답 반환 (Transactional이 끝나면서 UPDATE 쿼리 실행됨)
        return MemoUpdateResponse.from(memo);
    }

    @Transactional(readOnly = true)
    public MyMemoListResponse getMyMemos(Long userId, MemoType type, MemoStatus status, Long cursorId, int size) {

        Pageable pageable = PageRequest.of(0, size);
        List<Memo> memos = memoRepository.findAllMyMemos(userId, type, status, cursorId, pageable);

        Long nextCursorId = null;
        boolean hasNext = false;

        // 사이즈만큼 꽉 차서 왔다면 다음 페이지가 있을 확률이 높음 (간단한 처리)
        if (!memos.isEmpty() && memos.size() == size) {
            nextCursorId = memos.get(memos.size() - 1).getId();
            hasNext = true;
        }

        List<MyMemoListResponse.MyMemoItem> items = memos.stream()
                .map(memo -> MyMemoListResponse.MyMemoItem.builder()
                        .memoId(memo.getId())
                        .storeName(memo.getStore().getName())
                        .category(getCategoryName(memo.getMemoType())) // 한글 변환
                        .content(getPreview(memo.getContent())) // 미리보기 삽입
                        .status(memo.getStatus().name())
                        .createdAt(memo.getCreatedAt().toLocalDate().toString())
                        .ownerRead(memo.isOwnerRead())
                        .build())
                .collect(Collectors.toList());

        return MyMemoListResponse.builder()
                .memos(items)
                .nextCursorId(nextCursorId)
                .hasNext(hasNext)
                .build();
    }

    private String getCategoryName(MemoType type) {
        if (type == MemoType.ANSWER) return "고민 답변";
        if (type == MemoType.FREE) return "자유 메모";
        return "기타";
    }

    // 미리보기 만들기 (30글자 넘으면 자르고 ... 붙이기)
    private String getPreview(String content) {
        if (content == null) return ""; // 내용 없으면 빈칸
        if (content.length() > 30) {    // 30글자보다 길면?
            return content.substring(0, 30) + "..."; // 0부터 30번째까지만 자르고 ... 붙임
        }
        return content; // 짧으면 그냥 그대로 리턴
    }

    private final DecisionRepository decisionRepository;


    @Transactional(readOnly = true)
    public MyMemoDetailResponse getMemoDetail(Long userId, Long memoId) {
        // 1. 메모 조회 (내 아이디와 일치하는지 확인)
        Memo memo = memoRepository.findByIdAndUser_Id(memoId, userId)
                .orElseThrow(() -> new IllegalArgumentException("메모를 찾을 수 없습니다."));

        // 2. 사장님 고민 내용 가져오기
        String concernContent = null;
        if (memo.getConcern() != null) {
            concernContent = memo.getConcern().getContent();
        }

        // 3. 사장님 답글(Decision) 가져오기
        String ownerReply = null;
        Optional<Decision> decision = decisionRepository.findByMemoId(memoId);
        if (decision.isPresent() && decision.get().getMessage() != null) {
            ownerReply = decision.get().getMessage();
        }

        // 4. [추가] 이미지 리스트 변환 (MemoImage 엔티티 -> String URL)
        List<String> imageUrls = memo.getImages().stream()
                .map(MemoImage::getImageUrl) // 이미지 객체에서 URL만 추출
                .collect(Collectors.toList());

        // 5. DTO 변환
        return MyMemoDetailResponse.builder()
                .memoId(memo.getId())
                .storeId(memo.getStore().getId())
                .storeName(memo.getStore().getName())
                .concernContent(concernContent)
                .memoTitle(memo.getTitle())
                .memoContent(memo.getContent())
                .images(imageUrls)                // [추가] 여기에 담기
                .ownerReply(ownerReply)
                .status(memo.getStatus().name())
                .createdAt(memo.getCreatedAt().toString())
                .build();
    }
}