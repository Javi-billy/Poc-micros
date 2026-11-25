package com.example.demo.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.demo.application.service.RegistrarPedidoService;
import com.example.demo.domain.port.in.RegistrarPedidoUseCase;
import com.example.demo.domain.port.out.PedidoRepositoryPort;

@Configuration
public class BeanConfig {

	@Bean
	RegistrarPedidoUseCase registrarPedidoUseCase(PedidoRepositoryPort pedidoRepositoryPort) {
		return new RegistrarPedidoService(pedidoRepositoryPort);
	}
}
