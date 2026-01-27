package com.posit.posit.domain.map.controller;

import com.posit.posit.domain.map.dto.request.MapStoreListQuery;
import com.posit.posit.domain.map.dto.request.MapStoreMarkerQuery;
import com.posit.posit.domain.map.dto.response.MapStoreListResponse;
import com.posit.posit.domain.map.dto.response.MapStoreMarkerResponse;
import com.posit.posit.domain.map.service.MapService;
import com.posit.posit.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Map", description = "지도 화면을 위한 API")
@RestController
@RequestMapping("/map")
@RequiredArgsConstructor
public class MapController {
    private final MapService mapService;

    @GetMapping("/stores/markers")
    public ApiResponse<MapStoreMarkerResponse> markers(@Valid MapStoreMarkerQuery query) {
        return ApiResponse.success(mapService.getMarkers(query));
    }

    @GetMapping("/stores")
    public ApiResponse<MapStoreListResponse> list(@Valid MapStoreListQuery query) {
        MapService.ListResult result = mapService.getList(query);
        return ApiResponse.success(result.data(), result.meta());
    }
}
