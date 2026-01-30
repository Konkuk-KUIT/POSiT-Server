package com.posit.posit.domain.map.controller;

import com.posit.posit.domain.map.dto.request.MapStoreListQuery;
import com.posit.posit.domain.map.dto.request.MapStoreMarkerQuery;
import com.posit.posit.domain.map.dto.response.MapStoreDetailResponse;
import com.posit.posit.domain.map.dto.response.MapStoreListResponse;
import com.posit.posit.domain.map.dto.response.MapStoreMarkerResponse;
import com.posit.posit.domain.map.service.MapService;
import com.posit.posit.global.response.ApiResponse;
import com.posit.posit.global.swagger.ApiErrorCodes;
import com.posit.posit.global.swagger.SwaggerErrorSet;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Map", description = "지도 화면을 위한 API")
@RestController
@RequestMapping("/map")
@RequiredArgsConstructor
public class MapController {
    private final MapService mapService;

    @Operation(summary = "지도 핀 조회", description = "가게 위치만 반환하는 가벼운 응답")
    @ApiErrorCodes(SwaggerErrorSet.DEFAULT)
    @GetMapping("/stores/markers")
    public ApiResponse<MapStoreMarkerResponse> markers(@Valid MapStoreMarkerQuery query) {
        return ApiResponse.success(mapService.getMarkers(query));
    }

    @Operation(summary = "가게 리스트 조회", description = "화면 아래 가게 리스트 조회")
    @ApiErrorCodes(SwaggerErrorSet.DEFAULT)
    @GetMapping("/stores")
    public ApiResponse<MapStoreListResponse> list(@Valid MapStoreListQuery query) {
        MapService.ListResult result = mapService.getList(query);
        return ApiResponse.success(result.data(), result.meta());
    }

    @Operation(summary = "가게 상세 정보", description = "가게 상세 정보 응답")
    @ApiErrorCodes(SwaggerErrorSet.MAP_DETAIL)
    @GetMapping("/stores/{storeId}")
    public ApiResponse<MapStoreDetailResponse> detail(@PathVariable Long storeId) {
        return ApiResponse.success(mapService.getDetail(storeId));
    }
}
