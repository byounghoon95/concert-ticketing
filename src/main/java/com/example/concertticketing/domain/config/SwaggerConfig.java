package com.example.concertticketing.domain.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 2.x.x 버전: localhost:8080/swagger-ui.html
 * 3.x.x 버전: localhost:8080/swagger-ui/index.html
 * */
@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .components(new Components())
                .info(apiInfo());
    }

    private Info apiInfo() {
        return new Info()
                .title("Concert Ticketing API SPEC")
                .description("API Specification About Concert Reservation.\n" +
                        "Every response is wrapped by CommonResponse.\n" +
                        "So example value and real server response will be different.")
                .version("1.0.0");
    }
}
