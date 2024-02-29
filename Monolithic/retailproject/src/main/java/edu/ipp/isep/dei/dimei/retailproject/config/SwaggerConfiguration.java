package edu.ipp.isep.dei.dimei.retailproject.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Retail Project API",
                description = "Documentation for the Retail Project Monolith version",
                version = "1.0.0"
        ),
        servers = {
                @Server(
                        description = "Test Env",
                        url = "http://localhost:8080"
                )
        }
)
public class SwaggerConfiguration {

}