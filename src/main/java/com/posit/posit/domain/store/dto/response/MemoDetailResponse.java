package com.posit.posit.domain.store.dto.response;

import com.posit.posit.domain.memo.entity.Memo;
import com.posit.posit.domain.memo.entity.MemoType;
import com.posit.posit.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class MemoDetailResponse {

    private Long memoId;
    private String memoType;

    private OriginalConcern originalConcern;
    private String freeType;

    private String title;
    private String content;
    private String imageUrl;
    private String status;
    private LocalDateTime createdAt;

    private WriterInfo writer;

    // 1. 기존 유지 (고민 정보)
    @Getter @Builder
    public static class OriginalConcern {
        private Long concernId;
        private String content;
    }

    // 2. [수정] 작성자 정보에 'profile' 추가
    @Getter @Builder
    public static class WriterInfo {
        private String name;    // 작성자 이름 (예: subinn)
        private String profile; // ★ UI 핵심: "여성 만 21세"
    }

    public static MemoDetailResponse from(Memo memo) {
        User user = memo.getUser();

        // --- 1. 원본 고민 연결 로직 (기존 유지) ---
        OriginalConcern concernDto = null;
        if (memo.getMemoType() == MemoType.ANSWER && memo.getConcern() != null) {
            concernDto = OriginalConcern.builder()
                    .concernId(memo.getConcern().getId())
                    .content(memo.getConcern().getContent())
                    .build();
        }

        // --- 2. 자유 메모 타입 로직 (기존 유지) ---
        String freeTypeStr = null;
        if (memo.getMemoType() == MemoType.FREE && memo.getFreeType() != null) {
            freeTypeStr = memo.getFreeType().name();
        }

        // --- 3. [추가] 나이/성별 계산 로직 ---
        String gender = "알수없음";
        if ("F".equals(user.getGender())) gender = "여성";
        else if ("M".equals(user.getGender())) gender = "남성";

        int age = 0;
        if (user.getBirth() != null) {
            int currentYear = LocalDate.now().getYear();
            int birthYear = user.getBirth().getYear(); // LocalDate 가정
            age = currentYear - birthYear;
        }
        String profileStr = String.format("%s 만 %d세", gender, age);

        // --- 4. 최종 빌드 ---
        return MemoDetailResponse.builder()
                .memoId(memo.getId())
                .memoType(memo.getMemoType().name())
                .originalConcern(concernDto)
                .freeType(freeTypeStr)
                .title(memo.getTitle())
                .content(memo.getContent())
                .imageUrl(memo.getImage())
                .status(memo.getStatus().name())
                .createdAt(memo.getCreatedAt())
                .writer(WriterInfo.builder()
                        .name(user.getName()) // 화면엔 이름이 나을 것 같아 getName() 사용 (기존 loginId 원하시면 변경 가능)
                        .profile(profileStr)  // ★ 추가된 프로필 정보
                        .build())
                .build();
    }
}