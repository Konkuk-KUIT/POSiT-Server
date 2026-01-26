package com.posit.posit.global.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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

    private Info apiInfo() {
        return new Info()
                .title("POSiT Swagger")
                .description("POSiT 서비스 REST API")
                .version("1.0.0");
    }
}
