package com.posit.posit.domain.map.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.posit.posit.domain.map.dto.request.MapStoreListQuery;
import com.posit.posit.domain.map.dto.request.MapStoreMarkerQuery;
import com.posit.posit.domain.map.dto.response.MapStoreListResponse;
import com.posit.posit.domain.map.dto.response.MapStoreMarkerResponse;
import com.posit.posit.domain.store.repository.StoreRepository;
import com.posit.posit.global.response.Meta;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MapService {

    private final int DEFAULT_MARKER_LIMIT = 200;
    private final int DEFAULT_LIST_LIMIT = 20;

    private final StoreRepository storeRepository;
    public MapStoreMarkerResponse getMarkers(MapStoreMarkerQuery query) {
        int limit = (query.limit() == null) ? DEFAULT_MARKER_LIMIT : query.limit();

        List<MapStoreMarkerResponse.MarkerStore> stores =
                storeRepository.findMarkers(
                        query.swLat(), query.swLng(), query.neLat(), query.neLng(),
                        emptyToNull(query.keyword()), emptyToNull(query.type()),
                        limit
                );
        return new MapStoreMarkerResponse(stores);
    }

    /**
     * 지도 하단 리스트 조회 (거리순 + 커서 페이징)
     */
    public ListResult getList(MapStoreListQuery query) {
        int limit = (query.limit() == null) ? DEFAULT_LIST_LIMIT : query.limit();

        CursorPayload cursor = decodeCursorIfPresent(query.cursor());

        // limit+1로 조회해서 hasNext 판단
        int fetchSize = limit + 1;

        List<MapStoreQueryRepository.ListRow> rows =
                mapStoreQueryRepository.findList(
                        query.swLat(), query.swLng(), query.neLat(), query.neLng(),
                        query.myLat(), query.myLng(),
                        emptyToNull(query.keyword()), emptyToNull(query.type()),
                        cursor,
                        fetchSize
                );

        boolean hasNext = rows.size() > limit;
        if (hasNext) {
            rows = rows.subList(0, limit);
        }

        // storeId 목록
        List<Long> storeIds = rows.stream().map(MapStoreQueryRepository.ListRow::storeId).toList();

        // 대표 이미지(1장)만 가져오기: sort_order가 가장 작은 1개
        // (마커에는 이미지가 필요 없으므로 list에서만 수행)
        var thumbMap = mapStoreQueryRepository.findPrimaryThumbnails(storeIds);

        // statusCode 계산은 DB가 아니라 store 컬럼(open_time/not_open) 기반으로 서비스에서 계산
        // → rows에 openTime, notOpen이 포함되어 있다고 가정하고 여기서 계산

        List<MapStoreListResponse.StoreItem> items = rows.stream().map(r -> {
            String statusCode = StoreOpenStatusCalculator.calculateStatusCode(
                    r.openTime(), r.notOpen()
            );

            var primary = thumbMap.get(r.storeId());
            List<MapStoreListResponse.ImageItem> images = (primary == null)
                    ? List.of()
                    : List.of(new MapStoreListResponse.ImageItem(primary.imageId(), primary.thumbnailUrl(), primary.sortOrder()));

            return new MapStoreListResponse.StoreItem(
                    r.storeId(),
                    r.name(),
                    r.address(),
                    r.lat(),
                    r.lng(),
                    r.description(),
                    statusCode,
                    images
            );
        }).toList();

        MapStoreListResponse data = new MapStoreListResponse(items);

        // nextCursor 생성
        String nextCursor = null;
        if (hasNext && !rows.isEmpty()) {
            var last = rows.get(rows.size() - 1);
            nextCursor = encodeCursor(new CursorPayload(last.distanceKey(), last.storeId()));
        }

        Meta meta = new Meta(nextCursor, hasNext);
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
    public record CursorPayload(long lastDistanceKey, long lastStoreId) {}

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
}
