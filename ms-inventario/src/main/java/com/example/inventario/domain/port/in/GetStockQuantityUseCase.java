package com.example.inventario.domain.port.in;

public interface GetStockQuantityUseCase {

	int getAvailableQuantity(String productCode);
}