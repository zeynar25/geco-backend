package com.example.geco.configs;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("GECO API")
                        .version("1.0")
                        .description("API documentation for GECO"));
    }
}

//OpenAPI JSON:
//http://localhost:8080/v3/api-docs
	
//Swagger UI:
//http://localhost:8080/swagger-ui/index.html