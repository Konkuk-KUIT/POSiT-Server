package com.posit.posit.domain.user.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.posit.posit.domain.user.entity.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class UserUpdateRequest {

    @NotNull(message = "성별은 필수입니다.")
    private Gender gender; // "MALE" or "FEMALE" (Enum으로 바로 매핑)

    @NotBlank(message = "이름은 필수입니다.")
    private String name;

    @NotBlank(message = "전화번호는 필수입니다.")
    @Pattern(regexp = "^\\d{10,11}$", message = "전화번호는 - 없이 숫자만 입력해주세요.")
    private String phone;

    @NotNull(message = "생년월일은 필수입니다.")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate birthDate;
}