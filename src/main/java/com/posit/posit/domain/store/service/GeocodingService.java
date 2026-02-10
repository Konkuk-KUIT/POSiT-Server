package com.posit.posit.domain.store.service;

import lombok.Getter;
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

    private final RestTemplate restTemplate = new RestTemplate(); // API 요청 도구

    @Value("${kakao.api.key}") // yml에 적은 키 가져오기
    private String apiKey;

    @Value("${kakao.api.url}") // yml에 적은 URL 가져오기
    private String apiUrl;

    // 주소 -> 좌표 변환 메서드
    public Coordinate getCoordinate(String address) {
        try {
            // 1. 헤더에 키 담기 (신분증 제시)
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "KakaoAK " + apiKey);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            // 2. 요청 URL 만들기 (주소 포함)
            String url = apiUrl + "?query=" + address;

            // 3. 카카오 서버에 요청 보내기
            ResponseEntity<KakaoGeoResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    KakaoGeoResponse.class
            );

            // 4. 응답 까보기 (결과가 있으면 좌표 리턴)
            if (response.getBody() != null && !response.getBody().getDocuments().isEmpty()) {
                KakaoGeoResponse.Document doc = response.getBody().getDocuments().get(0);
                // 카카오는 x가 경도(Lng), y가 위도(Lat)임!
                return new Coordinate(
                        Double.parseDouble(doc.getY()), // 위도 (Latitude)
                        Double.parseDouble(doc.getX())  // 경도 (Longitude)
                );
            }
        } catch (Exception e) {
            e.printStackTrace(); // 에러 나면 로그 찍기
        }

        // 실패하면 기본값 (0.0) 리턴 (혹은 예외 던지기 선택 가능)
        return new Coordinate(0.0, 0.0);
    }

    // 좌표 담을 통 (DTO)
    @Getter
    public static class Coordinate {
        private final double lat; // 위도
        private final double lon; // 경도

        public Coordinate(double lat, double lon) {
            this.lat = lat;
            this.lon = lon;
        }
    }

    // 카카오 응답 받을 통 (내부 클래스)
    @Getter
    static class KakaoGeoResponse {
        private List<Document> documents;

        @Getter
        static class Document {
            private String x; // 경도 (Longitude)
            private String y; // 위도 (Latitude)
        }
    }
}