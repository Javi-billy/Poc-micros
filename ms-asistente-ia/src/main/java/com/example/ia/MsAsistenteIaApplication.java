package com.example.ia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(servers = { @Server(url = "http://localhost:8089", description = "API Gateway") })
@SpringBootApplication
@EnableFeignClients
public class MsAsistenteIaApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsAsistenteIaApplication.class, args);
	}

}
