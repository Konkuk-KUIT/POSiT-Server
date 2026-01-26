package com.posit.posit.global.swagger;


import com.posit.posit.global.error.ErrorCode;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;

import java.util.LinkedHashMap;
import java.util.Map;

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

            for (ErrorCode errorCode : errorSet.getCodes()) {
                String status = String.valueOf(errorCode.getHttpStatus().value());

                ApiResponse oasApiResponse = new ApiResponse()
                        .description("[" + errorCode.getCode() + "] " + errorCode.getMessage())
                        .content(new Content().addMediaType(
                                MediaType.APPLICATION_JSON_VALUE,
                                new io.swagger.v3.oas.models.media.MediaType()
                                        .schema(new Schema<>().$ref("#/components/schemas/ErrorResponse"))
                                        .addExamples(
                                                errorCode.name(),
                                                new Example()
                                                        .summary(errorCode.name())
                                                        .value(exampleValue(errorCode))
                                        )
                        ));

                operation.getResponses().addApiResponse(status, oasApiResponse);
            }

            return operation;
        };
    }

    private Map<String, Object> exampleValue(ErrorCode errorCode) {
        Map<String, Object> v = new LinkedHashMap<>();
        v.put("isSuccess", false);
        v.put("errorCode", errorCode.name());
        v.put("code", errorCode.getCode());
        return v;
    }
}
