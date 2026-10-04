package com.deveshsharma.knowledgehub.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configures the metadata exposed by the generated OpenAPI document.
 */
@Configuration
public class OpenApiConfig {

    /**
     * Creates the application OpenAPI definition.
     *
     * @return OpenAPI metadata for KnowledgeHub
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("KnowledgeHub")
                        .version("1.0.0")
                        .description("Backend REST API services and test end-to-end documentation.")
                        .contact(new Contact().name("Devesh Sharma").email("deveshsharma7618@gmail.com")));
    }
}
