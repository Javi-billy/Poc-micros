package com.example.pedidos.infrastructure.adapter.out.feign.inventory;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "ms-inventario", url = "${clients.inventario.url}")
interface InventoryFeignClient {

	@GetMapping("/api/inventory/{productCode}")
	StockResponse checkStock(@PathVariable("productCode") String productCode, @RequestParam("quantity") int quantity);

	record StockResponse(String productCode, boolean available) {
	}
}
