package com.posit.posit.domain.store.dto.response;

import com.posit.posit.domain.memo.entity.Memo;
import com.posit.posit.domain.memo.entity.MemoImage;
import com.posit.posit.domain.memo.entity.MemoType;
import com.posit.posit.domain.user.entity.Gender;
import com.posit.posit.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class MemoDetailResponse {

    private Long memoId;
    private String memoType;       // ANSWER, FREE

    private OriginalConcern originalConcern; // 객체로 감쌈
    private String freeType;

    private String title;
    private String content;

    // [변경] 단일 String imageUrl -> 리스트 images
    private List<String> images;

    // [추가] 사장님 답글
    private String ownerReply;

    private String status;
    private LocalDateTime createdAt;

    private WriterInfo writer;     // 객체로 감쌈

    // --- 내부 클래스 (Nested Class) ---
    @Getter @Builder
    public static class OriginalConcern {
        private Long concernId;
        private String content;
    }

    @Getter @Builder
    public static class WriterInfo {
        private String name;    // 로그인 아이디
        private String profile; // "여성 만 21세"
    }

    // --- 변환 로직 (Factory Method) ---
    public static MemoDetailResponse from(Memo memo, String ownerReply) {
        User user = memo.getUser();

        // 1. 이미지 리스트 변환
        List<String> imageUrls = Collections.emptyList();
        if (memo.getImages() != null && !memo.getImages().isEmpty()) {
            imageUrls = memo.getImages().stream()
                    .map(MemoImage::getImageUrl)
                    .collect(Collectors.toList());
        }

        // 2. 고민 정보 (ANSWER일 때만)
        OriginalConcern concernDto = null;
        if (memo.getMemoType() == MemoType.ANSWER && memo.getConcern() != null) {
            concernDto = OriginalConcern.builder()
                    .concernId(memo.getConcern().getId())
                    .content(memo.getConcern().getContent())
                    .build();
        }

        // 3. 자유 메모 타입 (FREE일 때만)
        String freeTypeStr = (memo.getFreeType() != null) ? memo.getFreeType().name() : null;

        // 4. 프로필 계산 (여성 만 21세)
        String gender = "알수없음";
        if (Gender.FEMALE.equals(user.getGender())) gender = "여성";
        else if (Gender.MALE.equals(user.getGender())) gender = "남성";

        int age = 0;
        if (user.getBirth() != null) {
            int currentYear = LocalDate.now().getYear();
            int birthYear = user.getBirth().getYear();
            age = currentYear - birthYear;
        }
        String profileStr = String.format("%s 만 %d세", gender, age);

        // 5. 최종 빌드
        return MemoDetailResponse.builder()
                .memoId(memo.getId())
                .memoType(memo.getMemoType().name())
                .originalConcern(concernDto)
                .freeType(freeTypeStr)
                .title(memo.getTitle())
                .content(memo.getContent())
                .images(imageUrls) // 리스트 주입
                .ownerReply(ownerReply) // 답글 주입
                .status(memo.getStatus().name())
                .createdAt(memo.getCreatedAt())
                .writer(WriterInfo.builder()
                        .name(user.getLoginId()) // 혹은 user.getName()
                        .profile(profileStr)
                        .build())
                .build();
    }
}