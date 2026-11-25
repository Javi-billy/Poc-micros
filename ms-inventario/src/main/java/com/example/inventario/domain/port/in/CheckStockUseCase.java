package com.example.inventario.domain.port.in;

public interface CheckStockUseCase {
	
	boolean hasAvailableStock(String productCode, int quantity);
}