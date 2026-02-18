package com.posit.posit.global.swagger;


import com.posit.posit.global.error.ErrorCode;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Configuration
public class SwaggerErrorResponseCustomizer {

    @Bean
    public OperationCustomizer apiErrorCustomizer() {
        return (operation, handlerMethod) -> {

            ApiErrorCodes annotation =
                    handlerMethod.getMethodAnnotation(ApiErrorCodes.class);

            if (annotation == null) {
                return operation;
            }

            SwaggerErrorSet errorSet = annotation.value();

            // status(400/404/409...) 별로 ErrorCode를 그룹핑해서
            // OpenAPI 응답은 status 당 1개만 만들고, 그 안에 examples를 여러 개 추가한다.
            Map<Integer, List<ErrorCode>> grouped = errorSet.getCodes().stream()
                    .filter(Objects::nonNull)
                    .collect(Collectors.groupingBy(
                            c -> c.getHttpStatus().value(),
                            LinkedHashMap::new,
                            Collectors.toList()
                    ));

            if (operation.getResponses() == null) {
                operation.setResponses(new ApiResponses());
            }

            for (Map.Entry<Integer, List<ErrorCode>> entry : grouped.entrySet()) {
                String status = String.valueOf(entry.getKey());
                List<ErrorCode> codes = entry.getValue();

                // 기존 status 응답이 있으면 재사용 (덮어쓰기 방지)
                ApiResponse apiResponse = operation.getResponses().get(status);
                if (apiResponse == null) {
                    apiResponse = new ApiResponse().description("Error Response");
                    operation.getResponses().addApiResponse(status, apiResponse);
                }

                // Content / application/json MediaType 준비
                Content content = apiResponse.getContent();
                if (content == null) {
                    content = new Content();
                    apiResponse.setContent(content);
                }

                MediaType jsonMediaType = content.get(org.springframework.http.MediaType.APPLICATION_JSON_VALUE);
                if (jsonMediaType == null) {
                    jsonMediaType = new MediaType()
                            .schema(new Schema<>().$ref("#/components/schemas/ErrorResponse"));
                    content.addMediaType(org.springframework.http.MediaType.APPLICATION_JSON_VALUE, jsonMediaType);
                } else if (jsonMediaType.getSchema() == null) {
                    jsonMediaType.schema(new Schema<>().$ref("#/components/schemas/ErrorResponse"));
                }

                // examples 맵 초기화
                if (jsonMediaType.getExamples() == null) {
                    jsonMediaType.setExamples(new LinkedHashMap<>());
                }

                // status 하나 아래 examples에 ErrorCode별 예시를 모두 추가
                for (ErrorCode errorCode : codes) {
                    String key = errorCode.name();
                    if (jsonMediaType.getExamples().containsKey(key)) {
                        continue;
                    }

                    Example ex = new Example()
                            .summary(errorCode.name())
                            .value(exampleValue(errorCode));

                    jsonMediaType.addExamples(key, ex);
                }
            }

            return operation;
        };
    }

    private Map<String, Object> exampleValue(ErrorCode errorCode) {
        Map<String, Object> v = new LinkedHashMap<>();
        v.put("isSuccess", false);
        v.put("errorCode", errorCode.name());
        v.put("message", errorCode.getMessage());
        v.put("code", errorCode.getCode());
        return v;
    }
}
