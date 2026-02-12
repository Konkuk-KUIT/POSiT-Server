package com.posit.posit.domain.user.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.posit.posit.domain.user.entity.Gender;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserUpdateRequest {

    private Gender gender; // "MALE" or "FEMALE" (Enum으로 바로 매핑)

    private String name;

    @Pattern(regexp = "^\\d{10,11}$", message = "전화번호는 - 없이 숫자만 입력해주세요.")
    private String phone;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate birthDate;

    private String password;
}