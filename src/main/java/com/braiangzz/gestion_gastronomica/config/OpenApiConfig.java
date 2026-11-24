package com.braiangzz.gestion_gastronomica.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de documentación Swagger / OpenAPI.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Gestión Gastronómica")
                        .version("1.0.0")
                        .description("Sistema de gestión de menú, pedidos y facturación"));
    }
}
