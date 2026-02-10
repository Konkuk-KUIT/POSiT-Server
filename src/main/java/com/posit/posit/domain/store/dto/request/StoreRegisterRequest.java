package com.posit.posit.domain.store.dto.request;

import com.posit.posit.domain.store.entity.StoreCategory;
import com.posit.posit.domain.store.entity.Weekday;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    // 직원 인증용 비밀번호
    @NotBlank(message = "직원 인증 비밀번호(4자리)를 설정해주세요.")
    private String couponPin;

    private List<String> imageUrls;

    @NotNull
    private OperationDto operation;

    private List<String> convinces;

    private List<MenuDto> menus;

    // 👇 [수정됨] lat, lng 필드 삭제 & roadAddress 필수 체크 추가
    @Getter
    @NoArgsConstructor
    public static class AddressDto {
        @NotBlank(message = "도로명 주소는 필수입니다.")
        private String roadAddress;   // 지오코딩의 기준이 됨

        private String detailAddress; // 상세 주소

        // private BigDecimal lat;  <-- 삭제 (서버가 계산함)
        // private BigDecimal lng;  <-- 삭제 (서버가 계산함)
    }

    @Getter
    @NoArgsConstructor
    public static class OperationDto {
        private List<Weekday> regularHolidays;
        private List<Weekday> openDay;
        private String openTime;
        private String closeTime;
    }

    @Getter
    @NoArgsConstructor
    public static class MenuDto {
        private String name;
        private Integer price;
        private String imageUrl;
    }
}