package com.posit.posit.domain.store.dto.request;

import com.posit.posit.domain.store.entity.StoreFilterCategory;
import com.posit.posit.domain.store.entity.Weekday;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public record StoreUpdateRequest(
        String name,
        AddressDto address,
        StoreFilterCategory type,
        String phone,
        String snsUrl,
        String description,
        List<String> imageUrls,
        OperationDto operation,
        List<String> convinces,
        List<MenuDto> menus
) {
    @Getter
    @NoArgsConstructor
    public static class AddressDto {
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
