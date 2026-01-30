package com.posit.posit.domain.store.service;

import com.posit.posit.domain.coupon.dto.request.CouponUseRequest;
import com.posit.posit.domain.coupon.entity.CouponTemplate;
import com.posit.posit.domain.coupon.entity.IssuedCoupon;
import com.posit.posit.domain.coupon.entity.IssuedCouponStatus;
import com.posit.posit.domain.coupon.repository.CouponTemplateRepository;
import com.posit.posit.domain.coupon.repository.IssuedCouponRepository;
import com.posit.posit.domain.memo.entity.Decision;
import com.posit.posit.domain.memo.entity.DecisionType;
import com.posit.posit.domain.memo.entity.Memo;
import com.posit.posit.domain.memo.entity.MemoStatus;
import com.posit.posit.domain.memo.repository.DecisionRepository;
import com.posit.posit.domain.store.dto.request.*;
import com.posit.posit.domain.store.dto.response.CouponTemplateResponse;
import com.posit.posit.domain.concern.entity.Concern;
import com.posit.posit.domain.store.dto.response.InboxMemoResponse;
import com.posit.posit.domain.store.dto.response.InboxResponse;
import com.posit.posit.domain.store.dto.response.OwnerHomeResponse;
import com.posit.posit.domain.store.entity.*;
import com.posit.posit.domain.store.repository.*;
import com.posit.posit.domain.user.entity.OwnerProfile;
import com.posit.posit.domain.user.entity.User;
import com.posit.posit.domain.user.repository.OwnerProfileRepository;
import com.posit.posit.domain.user.repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OwnerService {

    private final CouponTemplateRepository couponTemplateRepository;
    private final UserRepository userRepository; // [변경] Store 대신 User 필요

    // 1. 쿠폰 템플릿 생성
    @Transactional
    public Long createCouponTemplate(Long userId, CouponTemplateCreateRequest request) {

        // 사장님(User) 정보 가져오기 (Proxy 객체로 성능 최적화)
        User owner = userRepository.getReferenceById(userId);

        // 엔티티 생성 (기존에 있던 Entity 빌더 사용)
        CouponTemplate template = CouponTemplate.builder()
                .createdBy(owner) // [핵심] Store가 아니라 User를 넣음
                .title(request.getTitle())
                .description(request.getDescription())
                .image(request.getImage())
                .validDays(request.getValidDays())
                .build();

        return couponTemplateRepository.save(template).getId();
    }

    // 2. 쿠폰 템플릿 목록 조회
    public List<CouponTemplateResponse> getCouponTemplates(Long userId) {
        // 사장님 ID로 조회
        List<CouponTemplate> templates = couponTemplateRepository.findAllByCreatedById(userId);

        return templates.stream()
                .map(t -> CouponTemplateResponse.builder()
                        .templateId(t.getId())
                        .title(t.getTitle())
                        .description(t.getDescription())
                        .validDays(t.getValidDays())
                        .build())
                .collect(Collectors.toList());
    }


    private final ConcernRepository concernRepository;
    private final StoreRepository storeRepository;

    //고민 등록
    @Transactional
    public Long createConcern(Long userId, Long storeId, ConcernCreateRequest request) {

        // 1. 가게 조회
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 가게입니다."));

        // [검증] 요청한 사람이 진짜 이 가게 주인인가? (Store 테이블 owner_id 확인)
        // (Store 엔티티에 getOwner()나 getOwnerId()가 있다고 가정)
        // if (!store.getOwner().getId().equals(userId)) {
        //    throw new IllegalArgumentException("본인의 가게에만 고민을 등록할 수 있습니다.");
        // }

        // 2. 쿠폰 템플릿 조회
        CouponTemplate template = couponTemplateRepository.findById(request.getTemplateId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 쿠폰 템플릿입니다."));

        // [검증] 이 쿠폰 템플릿을 이 사장님이 만든 게 맞는가?
        if (!template.getCreatedBy().getId().equals(userId)) {
            throw new IllegalArgumentException("본인이 생성한 쿠폰 템플릿만 사용할 수 있습니다.");
        }

        // 3. 엔티티 생성
        Concern concern = Concern.builder()
                .store(store)
                .template(template)
                .content(request.getContent())
                .build();

        // 4. 저장
        return concernRepository.save(concern).getId();
    }

    private final MemoRepository memoRepository;

    // 3. 수신함 조회 (무한 스크롤 적용)
    @Transactional(readOnly = true)
    public InboxResponse getInbox(Long storeId, String tab, Long cursorId, int limit) {

        // 1. 다음 페이지 있는지 확인하기 위해 limit + 1개를 조회함
        Pageable pageable = PageRequest.of(0, limit + 1);
        List<Memo> memos;

        // 2. 탭 별 쿼리 실행
        switch (tab) {
            case "ANSWER":
                memos = memoRepository.findAnswers(storeId, cursorId, pageable);
                break;
            case "FREE":
                memos = memoRepository.findFreeMemos(storeId, cursorId, pageable);
                break;
            case "ADOPTED":
                memos = memoRepository.findAdoptedMemos(storeId, cursorId, pageable);
                break;
            case "REVIEWING": // 전체 대기
            default:
                memos = memoRepository.findAllReviewing(storeId, cursorId, pageable);
                break;
        }

        // 3. 무한 스크롤 메타데이터 계산
        boolean hasNext = false;
        Long nextCursor = null;

        if (memos.size() > limit) { // 요청한 것보다 하나 더 왔다면? 다음 페이지 있음!
            hasNext = true;
            memos.remove(limit); // 확인용으로 가져온 마지막 하나는 제거 (프론트엔드엔 limit개만 줌)
            nextCursor = memos.get(memos.size() - 1).getId(); // 마지막 아이템 ID가 다음 커서
        } else if (!memos.isEmpty()) {
            // 끝 페이지가 아닌데 데이터가 있는 경우 (다음 페이지는 없음)
            nextCursor = memos.get(memos.size() - 1).getId();
        }

        // 4. DTO 변환
        List<InboxMemoResponse> memoDtos = memos.stream()
                .map(InboxMemoResponse::from)
                .collect(Collectors.toList());

        return InboxResponse.builder()
                .memos(memoDtos)
                .meta(InboxResponse.Meta.builder()
                        .hasNext(hasNext)
                        .nextCursor(nextCursor)
                        .build())
                .build();
    }

    private final IssuedCouponRepository issuedCouponRepository;
    private final DecisionRepository decisionRepository;

    // 5-1. 답변 채택 (ADOPT)
    @Transactional
    public void adoptMemo(Long userId, Long memoId, MemoAdoptRequest request) {

        // 1. 메모 조회
        Memo memo = memoRepository.findById(memoId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메모입니다."));

        // [검증] 이미 처리된 메모인지 확인
        if (memo.getStatus() != MemoStatus.REVIEWING) {
            throw new IllegalStateException("이미 처리된 메모입니다.");
        }

        // 2. 쿠폰 템플릿 조회
        CouponTemplate template = couponTemplateRepository.findById(request.getCouponTemplateId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 쿠폰 템플릿입니다."));

        // 3. 메모 상태 변경
        memo.updateStatus(MemoStatus.ADOPTED);

        // 4. IssuedCoupon 생성 (보내주신 Entity Builder 사용)
        LocalDateTime expiredAt = LocalDateTime.now().plusDays(template.getValidDays());

        IssuedCoupon issuedCoupon = IssuedCoupon.builder()
                .store(memo.getStore())
                .user(memo.getUser())
                .memo(memo)
                .template(template)  // [중요] 엔티티에 template FK가 있어서 넣어줘야 함
                .title(template.getTitle())
                .description(template.getDescription())
                .image(template.getImage())
                .condition("유효기간 내 사용") // Entity에 condition이 not null이라 값 필요
                .expiredAt(expiredAt)
                .status(IssuedCouponStatus.ISSUED)
                // .issuedAt() -> insertable=false이므로 DB가 자동 생성 (생략)
                .build();

        issuedCouponRepository.save(issuedCoupon);

        // 5. Decision 생성 (채택 기록)
        Decision decision = Decision.builder()
                .memo(memo)
                .type(DecisionType.ADOPT)
                .couponTemplate(template) // 채택일 땐 어떤 템플릿인지 기록
                .message(request.getMessage())
                .build();

        decisionRepository.save(decision);
    }

    // 5-2. 답변 거절 (REJECT)
    @Transactional
    public void rejectMemo(Long userId, Long memoId, MemoRejectRequest request) {
        Memo memo = memoRepository.findById(memoId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메모입니다."));

        // 상태 변경
        memo.updateStatus(MemoStatus.REJECTED);

        // Decision 생성 (거절 기록)
        Decision decision = Decision.builder()
                .memo(memo)
                .type(DecisionType.REJECT)
                .rejectCode(request.getRejectCode()) // 거절 코드 저장
                .message(request.getMessage())
                .build();

        decisionRepository.save(decision);
    }

    // ... Dependencies (MemoRepository, IssuedCouponRepository, StoreRepository) ...

    // 5. 사장님 홈 화면 조회
    @Transactional(readOnly = true)
    public OwnerHomeResponse getOwnerHome(Long userId, Long storeId) {

        // 1. 가게 및 사장님 정보 조회
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 가게입니다."));

        // 사장님 닉네임 (Store 엔티티가 User를 참조하고 있다고 가정)
        // 만약 store.getOwner()가 없다면 userRepository.findById(userId)를 써야 함
        String nickname = store.getOwner().getName(); // 또는 getLoginId()

        // 2. 통계 데이터 카운팅
        // (1) 누적 메모 수
        long totalMemo = memoRepository.countByStoreId(storeId);

        // (2) 반영 완료 수 (ADOPTED)
        long adoptedMemo = memoRepository.countByStoreIdAndStatus(storeId, MemoStatus.ADOPTED);

        // (3) 신규 메모 수 (REVIEWING)
        long newMemo = memoRepository.countByStoreIdAndStatus(storeId, MemoStatus.REVIEWING);

        // (4) 쿠폰 발행 수
        long issuedCoupon = issuedCouponRepository.countByStoreId(storeId);

// (1) 최신 고민글 3개 가져오기
        List<Concern> recentConcerns = concernRepository.findTop3ByStoreIdOrderByCreatedAtDesc(storeId);

        // (2) DTO로 변환하면서 댓글 수(count) 조회하기
        List<OwnerHomeResponse.HomeConcern> myConcernList = recentConcerns.stream()
                .map(concern -> {
                    long commentCount = memoRepository.countByConcernId(concern.getId());

                    return OwnerHomeResponse.HomeConcern.builder()
                            .concernId(concern.getId())
                            .content(concern.getContent())
                            .createdAt(concern.getCreatedAt())
                            .commentCount(commentCount) // 댓글 수
                            .build();
                })
                .collect(Collectors.toList());

        // 3. 최종 응답 생성
        return OwnerHomeResponse.builder()
                .storeName(store.getName())
                .ownerNickname(nickname)
                .newMemoCount(newMemo)
                .stats(OwnerHomeResponse.HomeStats.builder() // 통계
                        .totalMemoCount(totalMemo)
                        .issuedCouponCount(issuedCoupon)
                        .adoptedCount(adoptedMemo)
                        .build())
                .myConcerns(myConcernList) // 리스트 추가
                .build();
    }

    // 클래스 내부 필드에 PasswordEncoder 추가
    private final PasswordEncoder passwordEncoder;

    // 6. 쿠폰 사용 처리 (직원 인증 포함)
    @Transactional
    public void useCoupon(Long userId, Long couponId, CouponUseRequest request) {

        // 1. 쿠폰 조회
        IssuedCoupon coupon = issuedCouponRepository.findById(couponId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 쿠폰입니다."));

        // 2. 가게 정보 조회
        Store store = coupon.getStore();

        // 3. [핵심] 비밀번호(PIN) 검증
        String storedHash = store.getCouponPinHash(); // DB에 저장된 암호화된 비밀번호

        // 3-1. 가게에 비밀번호가 설정되어 있지 않은 경우
        if (storedHash == null || storedHash.isBlank()) {
            throw new IllegalStateException("매장에 직원 확인 비밀번호가 설정되지 않았습니다.");
        }

        // 3-2. 입력받은 비밀번호와 DB 해시값 비교
        if (!passwordEncoder.matches(request.getPassword(), storedHash)) {
            throw new IllegalArgumentException("직원 확인 비밀번호가 일치하지 않습니다.");
        }

        // 4. 검증 통과 -> 쿠폰 사용 처리 (상태 변경)
        coupon.use();
    }

    // ... Repository Import ...
    private final ConvinceRepository convinceRepository; // 추가
    private final StoreImageRepository storeImageRepository; // 추가
    private final MenuRepository menuRepository; // 추가
    private final StoreConvinceRepository storeConvinceRepository; // 추가
    private final OwnerProfileRepository ownerProfileRepository;
    // 가게 등록
    @Transactional
    public Long registerStore(Long userId, StoreRegisterRequest request) {

        // 1. 사장님(User) 조회
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 2. 사장님 프로필에서 '사업자 번호' 가져오기 (Request에서 제거됨)
        OwnerProfile profile = ownerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("사장님 프로필(사업자 정보)이 존재하지 않습니다. 먼저 사업자 인증을 진행해주세요."));
        String businessNumber = profile.getBusinessNumber();

        // 3. 데이터 가공
        // 영업시간 포맷팅 "10:00-22:00"
        String fullOpenTime = request.getOperation().getOpenTime() + "-" + request.getOperation().getCloseTime();

        // 휴무일 (DB 단일 컬럼 한계로 첫 번째 값만 저장, 없으면 NULL)
        Weekday notOpenDay = null;
        if (request.getOperation().getRegularHolidays() != null && !request.getOperation().getRegularHolidays().isEmpty()) {
            notOpenDay = request.getOperation().getRegularHolidays().get(0);
        }

        // 직원 확인 비밀번호 암호화
        String encodedPin = passwordEncoder.encode(request.getCouponPin());

        // 4. Store 엔티티 생성 및 저장
        Store store = Store.builder()
                .owner(owner)
                .name(request.getName())
                .phone(request.getPhone())        // [New] 전화번호
                .description(request.getDescription())
                .category(request.getType())
                .businessNumber(businessNumber)   // [New] DB에서 조회한 값
                .roadAddress(request.getAddress().getRoadAddress())
                .lotAddress(request.getAddress().getDetailAddress()) // 상세주소 매핑
                .latitude(request.getAddress().getLat())
                .longitude(request.getAddress().getLng())
                .openTime(fullOpenTime)
                .notOpen(notOpenDay)
                .snsLink(request.getSnsUrl())
                .couponPinHash(encodedPin)        // [New] 암호화된 PIN
                .build();

        storeRepository.save(store);

        // 5. 가게 이미지 저장
        if (request.getImageUrls() != null) {
            int order = 1;
            for (String url : request.getImageUrls()) {
                StoreImage image = StoreImage.builder()
                        .store(store)
                        .imageUrl(url)
                        .thumbnailUrl(url)
                        .sortOrder(order++)
                        .build();
                storeImageRepository.save(image);
            }
        }

        // 6. 메뉴 저장
        if (request.getMenus() != null) {
            for (StoreRegisterRequest.MenuDto menuDto : request.getMenus()) {
                Menu menu = Menu.builder()
                        .store(store)
                        .name(menuDto.getName())
                        .price(menuDto.getPrice())
                        .image(menuDto.getImageUrl())
                        .type(MenuType.MAIN) // Enum 필요 (없으면 생성)
                        .build();
                menuRepository.save(menu);
            }
        }

        // 7. 편의시설 저장
        if (request.getConvinces() != null) {
            for (String code : request.getConvinces()) {
                Convince convince = convinceRepository.findByCode(code)
                        .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 편의시설 코드입니다: " + code));

                StoreConvince storeConvince = StoreConvince.builder()
                        .store(store)
                        .convince(convince)
                        .build();
                storeConvinceRepository.save(storeConvince);
            }
        }

        return store.getId();
    }
}