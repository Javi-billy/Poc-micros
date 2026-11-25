package com.example.pedidos.infrastructure.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.pedidos.domain.service.CreateOrderService;
import com.example.pedidos.domain.port.in.CreateOrderUseCase;
import com.example.pedidos.domain.port.out.EventPublisherPort;
import com.example.pedidos.domain.port.out.InventoryPort;
import com.example.pedidos.domain.port.out.OrderRepositoryPort;

@Configuration
@EnableFeignClients(basePackages = "com.example.pedidos.infrastructure.adapter.out.feign")
public class BeanConfiguration {

	@Bean
	public CreateOrderUseCase createOrderUseCase(InventoryPort inventoryPort, OrderRepositoryPort orderRepositoryPort,
			EventPublisherPort eventPublisherPort) {
		return new CreateOrderService(inventoryPort, orderRepositoryPort, eventPublisherPort);
	}
}
