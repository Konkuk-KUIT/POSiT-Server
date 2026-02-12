package com.posit.posit.domain.store.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GeocodingService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${kakao.api.key}")
    private String apiKey;

    @Value("${kakao.api.url}") // https://dapi.kakao.com/v2/local/search/address.json
    private String apiUrl;

    public GeoResult getGeoData(String address) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "KakaoAK " + apiKey);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            String url = apiUrl + "?query={query}";

            ResponseEntity<KakaoGeoResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    KakaoGeoResponse.class,
                    address
            );

            if (response.getBody() != null && !response.getBody().getDocuments().isEmpty()) {
                KakaoGeoResponse.Document doc = response.getBody().getDocuments().get(0);

                // 1. 좌표 추출
                double lat = Double.parseDouble(doc.getY());
                double lon = Double.parseDouble(doc.getX());

                // 2. 지번 주소 추출 로직
                // (도로명으로 검색해도 address 객체 안에 지번 정보가 매핑되어 옴)
                String lotAddress = "";

                if (doc.getAddress() != null) {
                    // address 객체가 있으면 그 안의 address_name이 진짜 지번 주소
                    lotAddress = doc.getAddress().getAddress_name();
                } else {
                    // 없으면 fallback으로 전체 주소 이름 사용
                    lotAddress = doc.getAddress_name();
                }

                return new GeoResult(lat, lon, lotAddress);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 실패 시 빈 값 반환
        return new GeoResult(0.0, 0.0, "");
    }

    // 결과 반환용 DTO (좌표 + 지번주소)
    @Getter
    @AllArgsConstructor
    public static class GeoResult {
        private final double lat;
        private final double lon;
        private final String lotAddress; // 지번 주소
    }

    // 카카오 API 응답 매핑용 DTO
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class KakaoGeoResponse {
        private List<Document> documents;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        static class Document {
            private String x; // 경도
            private String y; // 위도

            private String address_name; // 전체 주소 문자열

            // ★ 지번 주소 상세 정보가 담긴 객체 (카카오가 내려줌)
            private AddressInfo address;

            @Data
            static class AddressInfo {
                private String address_name; // ★ 여기가 진짜 지번 주소
            }
        }
    }
}