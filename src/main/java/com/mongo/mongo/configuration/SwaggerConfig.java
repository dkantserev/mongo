package com.mongo.mongo.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    public SwaggerConfig() {
    }

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI().info(new Info().title("XlsxTOMongoDb")
                .description("Программа для создания и управления mongoDb базы из xlsx файла").version("0.1")
                .contact(new Contact().email("dkantserev@gmail.com")));
    }
}