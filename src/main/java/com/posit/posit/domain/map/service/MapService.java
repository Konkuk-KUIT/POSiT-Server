package com.posit.posit.domain.map.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.posit.posit.domain.map.dto.request.MapStoreListQuery;
import com.posit.posit.domain.map.dto.request.MapStoreMarkerQuery;
import com.posit.posit.domain.map.dto.response.MapStoreListResponse;
import com.posit.posit.domain.map.dto.response.MapStoreMarkerResponse;
import com.posit.posit.domain.store.repository.StoreMapRepository;
import com.posit.posit.domain.store.service.StoreOpenCalculator;
import com.posit.posit.global.response.Meta;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MapService {

    private static final int DEFAULT_MARKER_LIMIT = 200;
    private static final int DEFAULT_LIST_LIMIT = 20;

    private final StoreMapRepository storeMapRepository;
    private final ObjectMapper objectMapper;

    /**
     * 지도 마커 조회 (가벼운 응답)
     */
    public MapStoreMarkerResponse getMarkers(MapStoreMarkerQuery query) {
        int limit = (query.limit() == null) ? DEFAULT_MARKER_LIMIT : query.limit();

        List<MapStoreMarkerResponse.MarkerStore> stores = storeMapRepository
                .findMarkers(
                        query.swLat(), query.swLng(), query.neLat(), query.neLng(),
                        emptyToNull(query.keyword()), emptyToNull(query.type()),
                        limit
                )
                .stream()
                .map(p -> new MapStoreMarkerResponse.MarkerStore(
                        p.getStoreId(),
                        p.getName(),
                        p.getLat().doubleValue(),
                        p.getLng().doubleValue()
                ))
                .toList();

        return new MapStoreMarkerResponse(stores);
    }

    /**
     * 지도 하단 리스트 조회 (거리순 only + 커서 페이징)
     */
    public ListResult getList(MapStoreListQuery query) {
        int limit = (query.limit() == null) ? DEFAULT_LIST_LIMIT : query.limit();

        CursorPayload cursor = decodeCursorIfPresent(query.cursor());
        Long cursorDistanceKey = (cursor == null) ? null : cursor.lastDistanceKey();
        Long cursorStoreId = (cursor == null) ? null : cursor.lastStoreId();

        // limit+1로 조회해서 hasNext 판단
        int fetchSize = limit + 1;

        List<StoreMapRepository.StoreListRowProjection> rows = storeMapRepository.findMapStoreList(
                query.swLat(), query.swLng(), query.neLat(), query.neLng(),
                query.myLat(), query.myLng(),
                emptyToNull(query.keyword()), emptyToNull(query.type()),
                cursorDistanceKey, cursorStoreId,
                fetchSize
        );

        boolean hasNext = rows.size() > limit;
        if (hasNext) {
            rows = rows.subList(0, limit);
        }

        // 대표 썸네일 1장(sort_order = 1)
        List<Long> storeIds = rows.stream().map(StoreMapRepository.StoreListRowProjection::getStoreId).toList();
        Map<Long, StoreMapRepository.StorePrimaryThumbnailProjection> thumbMap = storeIds.isEmpty()
                ? Map.of()
                : storeMapRepository.findPrimaryThumbnails(storeIds)
                .stream()
                .collect(Collectors.toMap(
                        StoreMapRepository.StorePrimaryThumbnailProjection::getStoreId,
                        p -> p,
                        (a, b) -> a
                ));

        List<MapStoreListResponse.StoreItem> items = rows.stream().map(r -> {
            String statusCode = StoreOpenCalculator.calculateStatusCode(
                    r.getOpenTime(),
                    r.getNotOpen()
            );

            // 리스트에서는 address 한 줄만 내려줌 (도로명 | 지번)
            String address = buildSingleAddress(r.getRoadAddress(), r.getLotAddress());

            var primary = thumbMap.get(r.getStoreId());
            List<MapStoreListResponse.ImageItem> images = (primary == null)
                    ? List.of()
                    : List.of(new MapStoreListResponse.ImageItem(
                            primary.getImageId(),
                            primary.getThumbnailUrl(),
                            primary.getSortOrder()
                    ));

            return new MapStoreListResponse.StoreItem(
                    r.getStoreId(),
                    r.getName(),
                    address,
                    r.getLat().doubleValue(),
                    r.getLng().doubleValue(),
                    r.getDescription(),
                    statusCode,
                    images
            );
        }).toList();

        MapStoreListResponse data = new MapStoreListResponse(items);

        // nextCursor 생성
        String nextCursor = null;
        if (hasNext && !rows.isEmpty()) {
            var last = rows.get(rows.size() - 1);
            nextCursor = encodeCursor(new CursorPayload(last.getDistanceKey(), last.getStoreId()));
        }

        Meta meta = new Meta("DISTANCE", nextCursor, hasNext);
        return new ListResult(data, meta);
    }

    /**
     * list 응답은 ApiResponse.success(data, meta) 형태를 쓰기 위해 data/meta를 함께 반환
     */
    public record ListResult(MapStoreListResponse data, Meta meta) {}

    /**
     * distanceKey + storeId 기반 커서
     * - distanceKey: FLOOR(distanceMeters) 같은 정수화된 거리 값(미터)
     * - storeId: 동률 tie-breaker
     */
    public record CursorPayload(Long lastDistanceKey, Long lastStoreId) {}

    private CursorPayload decodeCursorIfPresent(String cursor) {
        if (!StringUtils.hasText(cursor)) return null;
        try {
            byte[] decoded = Base64.getUrlDecoder().decode(cursor);
            String json = new String(decoded, StandardCharsets.UTF_8);
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (Exception e) {
            // 커서가 깨졌으면 400으로 처리하는 게 더 낫지만, 여기서는 안전하게 null 처리
            return null;
        }
    }

    private String encodeCursor(CursorPayload payload) {
        try {
            String json = objectMapper.writeValueAsString(payload);
            return Base64.getUrlEncoder().withoutPadding().encodeToString(json.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            return null;
        }
    }

    private static String emptyToNull(String s) {
        return (s == null || s.isBlank()) ? null : s;
    }

    private static String buildSingleAddress(String roadAddress, String lotAddress) {
        boolean hasRoad = StringUtils.hasText(roadAddress);
        boolean hasLot = StringUtils.hasText(lotAddress);

        if (hasRoad && hasLot) return roadAddress + " | " + lotAddress;
        if (hasRoad) return roadAddress;
        if (hasLot) return lotAddress;
        return "";
    }
}
