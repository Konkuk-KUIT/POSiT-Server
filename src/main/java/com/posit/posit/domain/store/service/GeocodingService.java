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

    public Coordinate getCoordinate(String address) {
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
                    address // {query} 자리에 주소가 들어감
            );

            if (response.getBody() != null && !response.getBody().getDocuments().isEmpty()) {
                KakaoGeoResponse.Document doc = response.getBody().getDocuments().get(0);
                return new Coordinate(
                        Double.parseDouble(doc.getY()),
                        Double.parseDouble(doc.getX())
                );
            }
        } catch (Exception e) {
            e.printStackTrace(); // 에러 로그 확인용
        }
        return new Coordinate(0.0, 0.0);
    }

    @Getter
    public static class Coordinate {
        private final double lat;
        private final double lon;

        public Coordinate(double lat, double lon) {
            this.lat = lat;
            this.lon = lon;
        }
    }

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
        }
    }
}