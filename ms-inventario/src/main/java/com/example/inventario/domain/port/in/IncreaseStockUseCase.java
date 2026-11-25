package com.example.inventario.domain.port.in;

public interface IncreaseStockUseCase {

	void increaseStock(String productCode, int quantity);
}