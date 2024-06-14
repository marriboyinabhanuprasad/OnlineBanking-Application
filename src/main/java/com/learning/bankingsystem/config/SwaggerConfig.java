package com.learning.bankingsystem.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                title = "Trip Expenses Management System API",
                description = "TEMS API",
                summary = "API functionalities for TEMS",
                termsOfService = "T&C",
                contact = @Contact(
                        name = "Sairoopa",
                        email = "pundollasairoopa6@gmail.com"
                ),
                license = @License(
                        name = "Sai"
                ),
                version = "v3"

        ),
        servers = {
                @Server(
                        description = "Dev",
                        url = "http://localhost:8080"
                ),
                @Server(
                        description = "Test",
                        url = "http://localhost:8080"
                )
        },
        security = @SecurityRequirement(
                name = "JWT"
        )
)

@SecurityScheme(
        name = "JWT",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class SwaggerConfig {

}