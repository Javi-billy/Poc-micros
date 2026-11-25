package com.example.inventario.domain.port.in;

public interface DecreaseStockUseCase {

	void decreaseStock(String productCode, int quantity);
}
