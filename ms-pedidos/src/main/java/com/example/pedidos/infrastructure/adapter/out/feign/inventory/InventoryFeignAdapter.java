package com.example.pedidos.infrastructure.adapter.out.feign.inventory;

import org.springframework.stereotype.Component;

import com.example.pedidos.domain.port.out.InventoryPort;
import com.example.pedidos.infrastructure.adapter.out.feign.ExternalServiceUnavailableException;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@Component
public class InventoryFeignAdapter implements InventoryPort {

	private final InventoryFeignClient feignClient;

	public InventoryFeignAdapter(InventoryFeignClient feignClient) {
		this.feignClient = feignClient;
	}

	@Override
	@CircuitBreaker(name = "ms-inventario", fallbackMethod = "fallbackHasStock")
	public boolean hasStock(String productCode, int quantity) {
		var response = feignClient.checkStock(productCode, quantity);
		return response != null && response.available();
	}

	@SuppressWarnings("unused")
	private boolean fallbackHasStock(String productCode, int quantity, Throwable t) {
		System.err.println("CIRCUIT BREAKER [ms-inventario] -> Fallback activado. Motivo: " + t.getMessage());
		throw new ExternalServiceUnavailableException(
				"El servicio de inventario no está disponible temporalmente. Inténtalo en unos segundos.");
	}
}
