package com.example.inventario.domain.exception;

public class InsufficientStockException extends RuntimeException {
	
	public InsufficientStockException(String productCode) {
		super("No hay stock suficiente para descontar del producto: " + productCode);
	}
}
