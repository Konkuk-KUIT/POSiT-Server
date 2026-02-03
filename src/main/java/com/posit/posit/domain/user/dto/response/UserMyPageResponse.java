package com.posit.posit.domain.user.dto.response;

import com.posit.posit.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class UserMyPageResponse {

    private String loginId;   // 로그인 아이디
    private String name;      // 이름
    private String phone;     // 전화번호
    private LocalDate birthDate; // 생년월일 (JSON: "2003-12-08")
    private String gender;    // 성별 (JSON: "MALE")

    public static UserMyPageResponse from(User user) {
        return UserMyPageResponse.builder()
                .loginId(user.getLoginId())
                .name(user.getName())
                .phone(user.getPhone())
                .birthDate(user.getBirth()) // Entity의 필드명은 birth
                // Gender Enum을 String으로 변환 (null이면 null 반환)
                .gender(user.getGender() != null ? user.getGender().name() : null)
                .build();
    }
}