package com.mongo.mongo.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;

public class SwaggerConfig {

    public SwaggerConfig() {
    }

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI().info(new Info().title("XlsxTOMongoDb")
                .description("Программа для создания и управления mongoDb базы из xlsx файла"));
    }
}