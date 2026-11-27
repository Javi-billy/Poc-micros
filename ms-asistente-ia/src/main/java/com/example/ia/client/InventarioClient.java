package com.example.ia.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-inventario", url = "${clients.inventario.url}")
public interface InventarioClient {

	@GetMapping("/api/inventory/{productCode}/quantity")
	InventarioResponse consultarCantidad(@PathVariable("productCode") String productCode);

	record InventarioResponse(String productCode, int quantity) {
	}
}
