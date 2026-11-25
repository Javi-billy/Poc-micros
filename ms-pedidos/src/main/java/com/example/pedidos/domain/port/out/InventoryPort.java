package com.example.pedidos.domain.port.out;

public interface InventoryPort {
	boolean hasStock(String productCode, int quantity);
}