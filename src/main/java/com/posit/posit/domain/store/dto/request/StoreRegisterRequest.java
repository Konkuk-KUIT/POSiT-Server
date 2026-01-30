package com.posit.posit.domain.store.dto.request;

import com.posit.posit.domain.store.entity.StoreCategory;
import com.posit.posit.domain.store.entity.Weekday;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Getter
@NoArgsConstructor
public class StoreRegisterRequest {

    @NotBlank(message = "가게 이름은 필수입니다.")
    private String name;

    @NotNull(message = "주소 정보는 필수입니다.")
    private AddressDto address;

    @NotNull(message = "가게 종류는 필수입니다.")
    private StoreCategory type; // STUDY, BRUNCH ...

    @NotBlank(message = "전화번호는 필수입니다.")
    private String phone;

    private String snsUrl;

    @NotBlank(message = "가게 소개는 필수입니다.")
    private String description;

    // [중요] 직원 인증용 비밀번호 설정 (가입 시 설정)
    @NotBlank(message = "직원 인증 비밀번호(4자리)를 설정해주세요.")
    private String couponPin;

    private List<String> imageUrls;

    @NotNull
    private OperationDto operation;

    private List<String> convinces; // ["TAKEOUT", "WIFI"]

    private List<MenuDto> menus;

    @Getter
    @NoArgsConstructor
    public static class AddressDto {
        private String roadAddress;
        private String detailAddress;
        private BigDecimal lat;
        private BigDecimal lng;
    }

    @Getter
    @NoArgsConstructor
    public static class OperationDto {
        private List<Weekday> regularHolidays; // ["MON", "TUE"]
        private List<Weekday> openDay;
        private String openTime;  // "11:00"
        private String closeTime; // "22:00"
    }

    @Getter
    @NoArgsConstructor
    public static class MenuDto {
        private String name;
        private Integer price;
        private String imageUrl;
    }
}