package com.example.logitrack.config;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Hidden
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                // Información general de la API
                .info(new Info()
                        .title("Logitrack API")
                        .version("1.0")
                        .description("API REST para la gestión de bodegas, inventario y auditoría. "
                                + "Inicia sesión en /auth/login, copia el token y úsalo en el botón Authorize.")
                        .contact(new Contact()
                                .name("Equipo LogiTrack")
                                .email("soporte@logitrack.com")))

                // Autenticación Bearer (JWT)
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .bearerFormat("JWT")
                                        .scheme("bearer")));
    }
}

// http://localhost:8080/swagger-ui/index.html