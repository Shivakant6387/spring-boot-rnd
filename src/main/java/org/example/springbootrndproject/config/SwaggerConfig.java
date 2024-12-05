package org.example.springbootrndproject.config;

import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI springShopOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("RND API")
                        .description("RND API")
                        .contact(new Contact().email("shivakantsingh900@gmail.com").name("ShivaKant Singh"))
                )
                .externalDocs(new ExternalDocumentation());
    }
}