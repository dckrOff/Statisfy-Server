package uz.dckroff.statisfy.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
public class SpringDocConfig {

    @Bean
    public OpenApiCustomizer consumerTypeHeaderOpenAPICustomizer() {
        return openApi -> {
            // Добавляем дополнительную информацию к каждому endpoint
            openApi.getPaths().values().forEach(pathItem -> {
                pathItem.readOperations().forEach(operation -> {
                    operation.addExtension("x-api-version", "1.0");
                });
            });
        };
    }
} 