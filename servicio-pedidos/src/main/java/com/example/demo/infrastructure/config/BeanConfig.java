package com.example.demo.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.demo.application.service.RegistrarPedidoService;
import com.example.demo.domain.port.in.RegistrarPedidoUseCase;
import com.example.demo.domain.port.out.PedidoRepositoryPort;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class BeanConfig {

	@Bean
	ObjectMapper objectMapper() {
		return new ObjectMapper();
	}

	@Bean
	RegistrarPedidoUseCase registrarPedidoUseCase(PedidoRepositoryPort pedidoRepositoryPort) {
		return new RegistrarPedidoService(pedidoRepositoryPort);
	}
}
