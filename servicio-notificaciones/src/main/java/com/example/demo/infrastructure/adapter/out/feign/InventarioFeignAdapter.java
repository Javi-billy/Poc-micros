package com.example.demo.infrastructure.adapter.out.feign;

import org.springframework.stereotype.Component;
import com.example.demo.domain.port.out.InventarioPort;

@Component
public class InventarioFeignAdapter implements InventarioPort {

	private final InventoryFeignClient inventoryFeignClient;

	public InventarioFeignAdapter(InventoryFeignClient inventoryFeignClient) {
		this.inventoryFeignClient = inventoryFeignClient;
	}

	@Override
	public void descontarStock(String codigoProducto, int cantidad) {
		inventoryFeignClient.decreaseStock(codigoProducto, cantidad);
	}

	@Override
	public void incrementarStock(String codigoProducto, int cantidad) {
		inventoryFeignClient.increaseStock(codigoProducto, cantidad);
	}
}
