package com.posit.posit.global.swagger;

import com.posit.posit.global.error.ErrorResponse;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {
        String jwtSchemeName = "bearerAuth";

        return new OpenAPI()
                .info(apiInfo())
                .addSecurityItem(new io.swagger.v3.oas.models.security.SecurityRequirement()
                        .addList(jwtSchemeName))
                .components(new Components()
                        .addSecuritySchemes(jwtSchemeName,
                                new io.swagger.v3.oas.models.security.SecurityScheme()
                                        .name(jwtSchemeName)
                                        .type(io.swagger.v3.oas.models.security.SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                        )
                );
    }
    @Bean
    public OpenApiCustomizer registerErrorResponseSchema() {
        return openApi -> {
            if (openApi.getComponents() == null) {
                openApi.setComponents(new Components());
            }

            // ErrorResponse
            var erSchemas = ModelConverters.getInstance().read(ErrorResponse.class);
            erSchemas.forEach((name, schema) -> openApi.getComponents().addSchemas(name, schema));

            // FieldErrorDetail (nested)
            var feSchemas = ModelConverters.getInstance().read(ErrorResponse.FieldErrorDetail.class);
            feSchemas.forEach((name, schema) -> openApi.getComponents().addSchemas(name, schema));

            // 별칭 강제 추가: Swagger가 기대하는 정확한 키로도 등록
            io.swagger.v3.oas.models.media.Schema<?> fieldErrorSchema = null;

            // 1) 정확히 FieldErrorDetail 키가 있으면 그걸 사용
            if (feSchemas.containsKey("FieldErrorDetail")) {
                fieldErrorSchema = feSchemas.get("FieldErrorDetail");
            } else {
                // 2) 없으면 "FieldErrorDetail"을 포함하는 키를 찾아서 그걸 사용
                for (var e : feSchemas.entrySet()) {
                    if (e.getKey() != null && e.getKey().contains("FieldErrorDetail")) {
                        fieldErrorSchema = e.getValue();
                        break;
                    }
                }
            }

            if (fieldErrorSchema == null && !feSchemas.isEmpty()) {
                fieldErrorSchema = feSchemas.values().iterator().next();
            }

            if (fieldErrorSchema != null) {
                openApi.getComponents().addSchemas("FieldErrorDetail", fieldErrorSchema);
            }
        };
    }

    private Info apiInfo() {
        return new Info()
                .title("POSiT Swagger")
                .description("POSiT 서비스 REST API")
                .version("1.0.0");
    }
}
