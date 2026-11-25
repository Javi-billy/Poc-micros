package com.example.inventario.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

	@Value("${GATEWAY_URL:http://localhost:8089}")
	private String gatewayUrl;

	@Bean
	OpenAPI customOpenAPI() {
		return new OpenAPI().info(new Info().title("Documentación API").version("1.0"))
				.servers(List.of(new Server().url(gatewayUrl).description("API Gateway (8089)")));
	}
}