package hu.telco.integration.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI productIntegrationApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Telco Data Integration API")
                        .version("1.0.0")
                        .description("""
                                REST API for a unified product catalogue.

                                Integrates CSV and JSON product feeds,
                                validates individual records, and detects
                                conflicts between matching products.

                                Valid products may still contain conflicts.
                                """));
    }
}