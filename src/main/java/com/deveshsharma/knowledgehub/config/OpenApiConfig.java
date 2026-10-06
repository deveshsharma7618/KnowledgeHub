package com.deveshsharma.knowledgehub.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityScheme;
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
                        .description("""
                                KnowledgeHub REST API.

                                Authentication issues a short-lived access token and a refresh token.
                                Refresh tokens are stored as SHA-256 hashes in PostgreSQL; a new signup or
                                login revokes all previously active refresh tokens for that user. Access token
                                hashes are cached in Redis until the JWT expires, and every protected request
                                must pass both JWT validation and the Redis revocation check.

                                Errors use the ErrorResponse schema with title, status, and detail fields.
                                """)
                        .contact(new Contact().name("Devesh Sharma").email("deveshsharma7618@gmail.com")))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}
