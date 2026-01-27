package com.posit.posit.domain.store.repository;

import com.posit.posit.domain.store.entity.Store;
import com.posit.posit.domain.store.entity.Weekday;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface StoreMapRepository extends JpaRepository<Store, Long> {
    /** ------------------------------
     *  지도: 마커 조회 (가벼운 응답)
     *  - bounds + (keyword/type) 필터
     *  - 정렬/커서 없음
     *  - limit 상한은 컨트롤러/서비스에서 관리
     *  ------------------------------ */
    interface StoreMarkerProjection {
        Long getStoreId();
        String getName();
        BigDecimal getLat();
        BigDecimal getLng();
    }

    @Query(value = """
            SELECT
              s.id AS storeId,
              s.name AS name,
              s.latitude AS lat,
              s.longitude AS lng
            FROM store s
            WHERE s.latitude BETWEEN :swLat AND :neLat
              AND s.longitude BETWEEN :swLng AND :neLng
              AND (:keyword IS NULL OR s.name LIKE CONCAT('%', :keyword, '%'))
              AND (
                :type IS NULL OR EXISTS (
                  SELECT 1
                  FROM store_filter sf
                  JOIN `filter` f ON f.id = sf.filter_id
                  WHERE sf.store_id = s.id
                    AND f.category = 'TYPE'
                    AND f.code = :type
                )
              )
            LIMIT :limit
            """, nativeQuery = true)
    List<StoreMarkerProjection> findMarkers(
            @Param("swLat") double swLat,
            @Param("swLng") double swLng,
            @Param("neLat") double neLat,
            @Param("neLng") double neLng,
            @Param("keyword") String keyword,
            @Param("type") String type,
            @Param("limit") int limit
    );

    /** ------------------------------
     *  지도: 하단 리스트 조회 (거리순 only)
     *  - bounds + myLat/myLng + (keyword/type) + cursor
     *  - cursor는 distanceKey(정수) + storeId
     *  ------------------------------ */
    interface StoreListRowProjection {
        Long getStoreId();
        String getName();
        String getDescription();
        BigDecimal getLat();
        BigDecimal getLng();
        String getRoadAddress();
        String getLotAddress();
        String getOpenTime();
        Weekday getNotOpen();
        Long getDistanceKey();
        Double getDistanceMeters();
    }

    @Query(value = """
            SELECT *
            FROM (
              SELECT
                s.id AS storeId,
                s.name AS name,
                s.description AS description,
                s.latitude AS lat,
                s.longitude AS lng,
                s.road_address AS roadAddress,
                s.lot_address AS lotAddress,
                s.open_time AS openTime,
                s.not_open AS notOpen,
                FLOOR(
                  6371000 * ACOS(
                    COS(RADIANS(:myLat)) * COS(RADIANS(s.latitude))
                    * COS(RADIANS(s.longitude) - RADIANS(:myLng))
                    + SIN(RADIANS(:myLat)) * SIN(RADIANS(s.latitude))
                  )
                ) AS distanceKey,
                (
                  6371000 * ACOS(
                    COS(RADIANS(:myLat)) * COS(RADIANS(s.latitude))
                    * COS(RADIANS(s.longitude) - RADIANS(:myLng))
                    + SIN(RADIANS(:myLat)) * SIN(RADIANS(s.latitude))
                  )
                ) AS distanceMeters
              FROM store s
              WHERE s.latitude BETWEEN :swLat AND :neLat
                AND s.longitude BETWEEN :swLng AND :neLng
                AND (:keyword IS NULL OR s.name LIKE CONCAT('%', :keyword, '%'))
                AND (
                  :type IS NULL OR EXISTS (
                    SELECT 1
                    FROM store_filter sf
                    JOIN `filter` f ON f.id = sf.filter_id
                    WHERE sf.store_id = s.id
                      AND f.category = 'TYPE'
                      AND f.code = :type
                  )
                )
            ) t
            WHERE (
              :cursorDistanceKey IS NULL
              OR t.distanceKey > :cursorDistanceKey
              OR (t.distanceKey = :cursorDistanceKey AND t.storeId > :cursorStoreId)
            )
            ORDER BY t.distanceKey ASC, t.storeId ASC
            LIMIT :limit
            """, nativeQuery = true)
    List<StoreListRowProjection> findMapStoreList(
            @Param("swLat") double swLat,
            @Param("swLng") double swLng,
            @Param("neLat") double neLat,
            @Param("neLng") double neLng,
            @Param("myLat") double myLat,
            @Param("myLng") double myLng,
            @Param("keyword") String keyword,
            @Param("type") String type,
            @Param("cursorDistanceKey") Long cursorDistanceKey,
            @Param("cursorStoreId") Long cursorStoreId,
            @Param("limit") int limit
    );

    /**
     * 지도 리스트: 대표 썸네일 1장(sort_order = 1) 조회
     */
    interface StorePrimaryThumbnailProjection {
        Long getStoreId();
        Long getImageId();
        String getThumbnailUrl();
        Integer getSortOrder();
    }

    @Query(value = """
            SELECT
              si.store_id AS storeId,
              si.id AS imageId,
              si.thumbnail_url AS thumbnailUrl,
              si.sort_order AS sortOrder
            FROM store_image si
            WHERE si.store_id IN (:storeIds)
              AND si.sort_order = 1
            """, nativeQuery = true)
    List<StorePrimaryThumbnailProjection> findPrimaryThumbnails(@Param("storeIds") List<Long> storeIds);
}
