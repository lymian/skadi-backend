package com.skadi.backend.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("Skadi - Sistema Kardex Multi-Tenant API")
                        .version("1.0.0")
                        .description("""
                                API REST para el sistema de gestión de inventario Kardex multi-tenant.

                                ## Características principales:
                                - **Multi-tenancy**: Aislamiento por empresa
                                - **Kardex valorizado**: Costo promedio ponderado (WAC)
                                - **FIFO**: Salidas por lote más antiguo
                                - **Roles**: admin, operador, consulta

                                ## Autenticación:
                                1. Registrar empresa: `POST /auth/register`
                                2. Login: `POST /auth/login`
                                3. Usar token en header: `Authorization: Bearer <token>`
                                """)
                        .contact(new Contact()
                                .name("Skadi Team")
                                .email("soporte@skadi.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .addSecurityItem(new SecurityRequirement()
                        .addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Ingresa el token JWT obtenido del endpoint /auth/login")));
    }
}
