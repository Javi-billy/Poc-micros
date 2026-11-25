package com.example.inventario.infrastructure.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.inventario.domain.model.ProductStock;
import com.example.inventario.domain.port.out.ProductStockRepositoryPort;
import com.example.inventario.domain.service.InventoryService;

@Configuration
public class BeanConfiguration {

	@Bean
	InventoryService inventoryService(ProductStockRepositoryPort repositoryPort) {
		return new InventoryService(repositoryPort);
	}

	@Bean
	CommandLineRunner initData(ProductStockRepositoryPort repositoryPort) {
		return args -> {
			repositoryPort.save(new ProductStock("PROD-100", 50));
			repositoryPort.save(new ProductStock("PROD-200", 5));
		};
	}
}