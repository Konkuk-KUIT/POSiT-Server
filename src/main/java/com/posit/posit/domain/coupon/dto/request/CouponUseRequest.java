package com.posit.posit.domain.coupon.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CouponUseRequest {

    @NotBlank(message = "직원 확인 비밀번호를 입력해주세요.")
    private String password; // 직원이 입력한 숫자 (예: "1234")
}