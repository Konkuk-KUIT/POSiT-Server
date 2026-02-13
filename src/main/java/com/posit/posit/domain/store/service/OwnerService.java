package com.posit.posit.domain.store.service;

import com.posit.posit.domain.coupon.dto.request.CouponTemplateUpdateRequest;
import com.posit.posit.domain.coupon.dto.response.CouponTemplateUpdateResponse;
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
import com.posit.posit.domain.memo.repository.MemoRepository;
import com.posit.posit.domain.store.dto.request.*;
import com.posit.posit.domain.store.dto.response.*;
import com.posit.posit.domain.concern.entity.Concern;
import com.posit.posit.domain.store.entity.*;
import com.posit.posit.domain.store.repository.*;
import com.posit.posit.domain.user.entity.OwnerProfile;
import com.posit.posit.domain.user.entity.User;
import com.posit.posit.domain.user.repository.OwnerProfileRepository;
import com.posit.posit.domain.user.repository.UserRepository;
import com.posit.posit.global.error.CustomException;
import com.posit.posit.global.error.ErrorCode;
import org.springdoc.webmvc.core.service.RequestService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OwnerService {

    private final CouponTemplateRepository couponTemplateRepository;
    private final UserRepository userRepository; // [변경] Store 대신 User 필요
    private final RequestService requestBuilder;
    private final StoreFilterRepository storeFilterRepository;
    private final FilterRepository filterRepository;
    private final ConvinceRepository convinceRepository;
    private final StoreImageRepository storeImageRepository;
    private final MenuRepository menuRepository;
    private final StoreConvinceRepository storeConvinceRepository;
    private final OwnerProfileRepository ownerProfileRepository;
    private final GeocodingService geocodingService;
    private final IssuedCouponRepository issuedCouponRepository;
    private final DecisionRepository decisionRepository;
    private final ConcernRepository concernRepository;
    private final StoreRepository storeRepository;
    private final MemoRepository memoRepository;
    private final PasswordEncoder passwordEncoder;

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

    // 3. 수신함 조회 (무한 스크롤 적용)
    @Transactional(readOnly = true)
    public Slice<InboxMemoResponse> getInbox(Long ownerId, String tab, Long cursorId, int limit) {

        Store store = storeRepository.findByOwnerId(ownerId)
                .orElseThrow(() -> new CustomException(ErrorCode.STORE_NOT_FOUND));
        Long storeId = store.getId();

        Pageable pageable = PageRequest.of(0, limit + 1);
        List<Memo> memos;

        // 2. 탭별 쿼리 실행
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
            case "REVIEWING":
            default:
                memos = memoRepository.findAllReviewing(storeId, cursorId, pageable);
                break;
        }

        // 3. hasNext 계산
        boolean hasNext = false;
        if (memos.size() > limit) {
            hasNext = true;
            memos.remove(limit); // 확인용 1개 제거
        }

        // 4. DTO 변환 (작성하신 InboxMemoResponse 사용)
        List<InboxMemoResponse> memoDtos = memos.stream()
                .map(InboxMemoResponse::from)
                .collect(Collectors.toList());

        // 5. Slice 반환 (데이터와 페이징 정보가 담김)
        return new SliceImpl<>(memoDtos, pageable, hasNext);
    }

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
        long totalCount = issuedCouponRepository.countByUserId(userId);

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
                        .issuedCouponCount(totalCount)
                        .adoptedCount(adoptedMemo)
                        .build())
                .myConcerns(myConcernList) // 리스트 추가
                .build();
    }

    // 가게 등록
    @Transactional
    public Long registerStore(Long userId, StoreRegisterRequest request) {

        // 1. 사장님(User) 조회
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 2. 사장님 프로필 확인
        OwnerProfile profile = ownerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("사장님 프로필(사업자 정보)이 존재하지 않습니다. 먼저 사업자 인증을 진행해주세요."));
        String businessNumber = profile.getBusinessNumber();

        // 3. [수정] 주소 데이터 가공 (도로명/지번 + 상세주소 합치기)
        String roadAddr = request.getAddress().getRoadAddress();
        String detailAddr = request.getAddress().getDetailAddress(); // 사용자 입력 상세주소 (예: 101호)
        if (detailAddr == null) detailAddr = ""; // null 방지

        // API 호출 (좌표 + 지번주소)
        GeocodingService.GeoResult geoResult = geocodingService.getGeoData(roadAddr);

        // 주소 뒤에 상세주소를 붙임
        String fullRoadAddr = roadAddr + " " + detailAddr;                 // 도로명 + 상세
        String fullLotAddr = geoResult.getLotAddress() + " " + detailAddr; // 지번 + 상세

        // 영업시간 포맷팅
        String fullOpenTime = request.getOperation().getOpenTime() + "-" + request.getOperation().getCloseTime();

        // 휴무일
        Weekday notOpenDay = null;
        if (request.getOperation().getRegularHolidays() != null && !request.getOperation().getRegularHolidays().isEmpty()) {
            notOpenDay = request.getOperation().getRegularHolidays().get(0);
        }

        // 비밀번호 암호화
        String encodedPin = passwordEncoder.encode(request.getCouponPin());

        // 4. Store 엔티티 생성 및 저장
        Store store = Store.builder()
                .owner(owner)
                .name(request.getName())
                .phone(request.getPhone())
                .description(request.getDescription())
                .category(StoreType.CAFE)
                .businessNumber(businessNumber)

                //상세주소까지 합쳐진 풀 주소 저장
                .roadAddress(fullRoadAddr)
                .lotAddress(fullLotAddr)

                .latitude(BigDecimal.valueOf(geoResult.getLat()))
                .longitude(BigDecimal.valueOf(geoResult.getLon()))

                .openTime(fullOpenTime)
                .notOpen(notOpenDay)
                .snsLink(request.getSnsUrl())
                .couponPinHash(encodedPin)
                .build();

        storeRepository.save(store);

        // TYPE 필터 연결
        if(request.getType() != null) {
            Filter typeFilter = filterRepository
                    .findByCategoryAndCode("TYPE", request.getType())
                    .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 필터 코드입니다 : " + request.getType().name()));

            StoreFilter storeFilter = StoreFilter.builder()
                    .store(store)
                    .filter(typeFilter)
                    .build();
            storeFilterRepository.save(storeFilter);
        }

        // 5. 이미지 저장
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
                        .type(MenuType.MAIN)
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

    // 가게 PIN 번호 수정
    @Transactional
    public void updateStorePin(Long ownerId, StorePinUpdateRequest request) {

        // 1. 가게 조회
        Store store = storeRepository.findByOwnerId(ownerId)
                .orElseThrow(() -> new CustomException(ErrorCode.STORE_NOT_FOUND));

        if(store.getCouponPinHash() == null || store.getCouponPinHash().isBlank()) {
            throw new IllegalArgumentException("가게 PIN이 아직 설정되지 않았습니다.");
        }

        // 현재 PIN 검증
        if (!passwordEncoder.matches(request.getCurrentPin(), store.getCouponPinHash())) {
            throw new CustomException(ErrorCode.INVALID_PIN);
        }

        String encodedPin = passwordEncoder.encode(request.getPin());
        store.updateCouponPin(encodedPin);
    }

    // 가게 PIN 번호 검증
    @Transactional(readOnly = true)
    public void verifyStorePin(Long ownerId, StorePinVerifyRequest request) {
        Store store = storeRepository.findByOwnerId(ownerId)
                .orElseThrow(() -> new CustomException(ErrorCode.STORE_NOT_FOUND));
        if (store.getCouponPinHash() == null || store.getCouponPinHash().isBlank()) {
            throw new IllegalArgumentException("가게 핀이 아직 설정되지 않았습니다.");
        }
        if (!passwordEncoder.matches(request.currentPin(), store.getCouponPinHash())) {
            throw new CustomException(ErrorCode.INVALID_PIN);
        }
    }

    // 고민 수정
    @Transactional
    public ConcernUpdateResponse updateConcern(Long userId, Long concernId, ConcernUpdateRequest request) {

        // 1. 고민 조회
        Concern concern = concernRepository.findById(concernId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 고민입니다."));

        // 2. 권한 검증 (내 가게의 고민이 맞는지?)
        if (!concern.getStore().getOwner().getId().equals(userId)) {
            throw new IllegalArgumentException("본인의 고민만 수정할 수 있습니다.");
        }

        // 3. 템플릿 조회 (변경될 수 있으므로 다시 조회)
        CouponTemplate template = couponTemplateRepository.findById(request.getTemplateId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 쿠폰 템플릿입니다."));

        // 4. 업데이트 실행 (Dirty Checking)
        concern.update(request.getConcernContent(), template);

        // 5. 응답 반환
        return ConcernUpdateResponse.from(concern);
    }

    // 고민 상세 조회
    @Transactional(readOnly = true)
    public ConcernDetailResponse getConcernDetail(Long userId, Long concernId) {

        // 1. 고민글 조회 (사장님 본인 글인지 확인 권장)
        Concern concern = concernRepository.findById(concernId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 고민입니다."));

        // (선택) 내 가게의 고민이 맞는지 검증 로직
        if (!concern.getStore().getOwner().getId().equals(userId)) {
            throw new IllegalArgumentException("본인의 고민글만 조회할 수 있습니다.");
        }

        // 2. 해당 고민에 달린 메모(답변)들 조회
        // MemoRepository에 List<Memo> findByConcernId(Long concernId); 필요
        List<Memo> memos = memoRepository.findByConcernIdOrderByCreatedAtDesc(concernId);

        // 3. DTO 합체
        return ConcernDetailResponse.of(concern, memos);
    }

    // 11. 쿠폰 템플릿 수정
    @Transactional
    public CouponTemplateUpdateResponse updateCouponTemplate(Long userId, Long templateId, CouponTemplateUpdateRequest request) {

        // 1. 템플릿 조회
        CouponTemplate template = couponTemplateRepository.findById(templateId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 쿠폰 템플릿입니다."));
//
//        // 2. 권한 검증 (내 가게의 템플릿인지?)
//        if (!template.getStore().getOwner().getId().equals(userId)) {
//            throw new IllegalArgumentException("본인의 템플릿만 수정할 수 있습니다.");
//        }

        // 3. 내용 수정 (Dirty Checking)
        template.update(
                request.getTitle(),
                request.getCondition(),
                request.getImage(),
                request.getValidDays()
        );

        // 4. 응답 반환
        return CouponTemplateUpdateResponse.from(template);
    }

    // 12. 메모 상세 조회
    @Transactional(readOnly = true)
    public MemoDetailResponse getMemoDetail(Long userId, Long memoId) { // type 제거!

        // 1. 메모 조회
        Memo memo = memoRepository.findById(memoId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메모입니다."));

        // 2. 권한 검증
        if (!memo.getStore().getOwner().getId().equals(userId)) {
            throw new IllegalArgumentException("해당 메모를 조회할 권한이 없습니다.");
        }


        // 4. DTO 변환
        return MemoDetailResponse.from(memo);
    }

    // 13. 쿠폰 관리 (통계 + 목록)
    @Transactional(readOnly = true)
    public CouponManagementResponse getCouponManagement(Long userId, Long cursorId, int size) {

        // 1. 통계 계산 (Repository 메서드명이 ByUserId로 변경됨)
        long totalCount = issuedCouponRepository.countByUserId(userId);
        long usedCount = issuedCouponRepository.countByUserIdAndStatus(userId, IssuedCouponStatus.USED);
        long unusedCount = totalCount - usedCount;

        CouponManagementResponse.CouponSummary summary = CouponManagementResponse.CouponSummary.builder()
                .totalIssuedCount(totalCount)
                .usedCount(usedCount)
                .unusedCount(unusedCount)
                .build();

        // 2. 목록 조회 (Cursor Pagination)
        // size + 1개를 가져와서 다음 페이지 존재 여부 확인
        Pageable pageRequest = PageRequest.of(0, size + 1);

        List<IssuedCoupon> coupons;
        if (cursorId == null) {
            // 첫 페이지
            coupons = issuedCouponRepository.findAllByUserIdOrderByIdDesc(userId, pageRequest);
        } else {
            // 다음 페이지 (커서 사용)
            coupons = issuedCouponRepository.findAllByUserIdAndIdLessThanOrderByIdDesc(userId, cursorId, pageRequest);
        }

        // 3. 다음 커서 및 hasNext 처리
        boolean hasNext = false;
        Long nextCursorId = null;

        if (coupons.size() > size) {
            hasNext = true;
            coupons.remove(size); // 확인용으로 가져온 +1개 제거
            nextCursorId = coupons.get(coupons.size() - 1).getId(); // 마지막 아이템 ID가 다음 커서
        }

        // 4. DTO 변환
        List<CouponManagementResponse.CouponItem> couponItems = coupons.stream()
                .map(CouponManagementResponse.CouponItem::from)
                .toList();

        return CouponManagementResponse.builder()
                .summary(summary)
                .items(couponItems)
                .nextCursorId(nextCursorId)
                .hasNext(hasNext)
                .build();
    }

    //사장님이 올린 고민들 조회하기
    public OwnerConcernListResponse getMyConcerns(Long userId, Long cursorId, int size) {

        // 1. Repository 호출 (Object[] -> [Concern, Count])
        Slice<Object[]> slice = concernRepository.findMyConcernsWithCount(
                userId,
                cursorId,
                PageRequest.of(0, size)
        );

        // 2. DTO 변환
        List<OwnerConcernListResponse.ConcernItem> items = slice.getContent().stream()
                .map(row -> {
                    Concern concern = (Concern) row[0];
                    Long count = (Long) row[1];
                    return OwnerConcernListResponse.ConcernItem.from(concern, count);
                })
                .collect(Collectors.toList());

        // 3. 다음 커서 계산
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