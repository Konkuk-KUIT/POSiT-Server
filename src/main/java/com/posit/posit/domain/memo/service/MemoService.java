package com.posit.posit.domain.memo.service;

import com.posit.posit.domain.concern.entity.Concern;
import com.posit.posit.domain.memo.dto.request.MemoCreateRequest;
import com.posit.posit.domain.memo.dto.request.MemoUpdateRequest;
import com.posit.posit.domain.memo.dto.response.MemoCreateResponse;
import com.posit.posit.domain.memo.dto.response.MemoUpdateResponse;
import com.posit.posit.domain.memo.dto.response.MyMemoDetailResponse;
import com.posit.posit.domain.memo.dto.response.MyMemoListResponse;
import com.posit.posit.domain.memo.entity.Decision;
import com.posit.posit.domain.memo.entity.Memo;
import com.posit.posit.domain.memo.entity.MemoStatus;
import com.posit.posit.domain.memo.entity.MemoType;
import com.posit.posit.domain.memo.repository.DecisionRepository;
import com.posit.posit.domain.store.entity.Store;
import com.posit.posit.domain.memo.repository.MemoRepository;
import com.posit.posit.domain.store.repository.StoreRepository;
import com.posit.posit.domain.user.entity.User;
import com.posit.posit.domain.user.repository.UserRepository;
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
                        .ownerRead(false) // DB 컬럼 부재로 false 고정
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

        // 2. 사장님 고민 내용 가져오기 (고민 답변인 경우)
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

        // 4. DTO 변환
        return MyMemoDetailResponse.builder()
                .memoId(memo.getId())
                .storeId(memo.getStore().getId())
                .storeName(memo.getStore().getName())
                .concernContent(concernContent)
                .memoTitle(memo.getTitle())       // 제목 매핑
                .memoContent(memo.getContent())   // 내용 매핑
                .ownerReply(ownerReply)           // 답글 매핑
                .status(memo.getStatus().name())
                .createdAt(memo.getCreatedAt().toString()) // 포맷팅 필요시 수정 (예: yyyy-MM-dd HH:mm)
                .build();
    }
}