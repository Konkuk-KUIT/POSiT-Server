package com.posit.posit.domain.store.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StorePinUpdateRequest {
    @NotBlank(message = "현재비밀번호를 입력해주세요.")
    @Pattern(regexp = "\\d{4}", message = "비밀번호는 4자리 숫자여야 합니다.")
    private String currentPin;
    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Pattern(regexp = "\\d{4}", message = "비밀번호는 4자리 숫자여야 합니다.")
    private String pin;
}