package com.example.demo.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.demo.application.service.ProcesarPedidoService;
import com.example.demo.domain.port.in.ProcesarPedidoUseCase;
import com.example.demo.domain.port.out.InventarioPort;

@Configuration
public class BeanConfig {

	@Bean
	ProcesarPedidoUseCase procesarPedidoUseCase(InventarioPort inventarioPort) {
		return new ProcesarPedidoService(inventarioPort);
	}
}