package com.posit.posit.domain.store.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class OwnerHomeResponse {

    private String storeName;     // "뉴베이크"
    private String ownerNickname; // "tera coffee_owner"

    private HomeStats stats;      // 중첩 객체

    private long newMemoCount;    // 신규 메모 ("신규 메모 보러가기" 옆 숫자)

    private List<HomeConcern> myConcerns;
    @Getter
    @Builder
    public static class HomeStats {
        private long totalMemoCount;    // 누적 메모
        private long issuedCouponCount; // 쿠폰 발행 수
        private long adoptedCount;      // 반영(채택) 완료 수
    }

    @Getter
    @Builder
    public static class HomeConcern {
        private Long concernId;
        private String content;      // "매장 조명을 조금 더..."
        private LocalDateTime createdAt; // 프론트에서 "2일 전"으로 계산
        private long commentCount;   // 말풍선 옆 숫자
    }

}