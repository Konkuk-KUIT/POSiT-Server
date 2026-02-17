package com.posit.posit.domain.store.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StoreUpdateRequest {
    private String name;            // 가게 이름
    private String description;     // 가게 설명
    private String address;         // 주소
    private String businessHours;   // 영업 시간 (예: "09:00~22:00")
    private String notOpen;         // 휴무일 (예: "FRI")
    private String phone;           // 전화번호
}