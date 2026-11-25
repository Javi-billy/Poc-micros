package com.example.inventario.domain.exception;

public class ProductNotFoundException extends RuntimeException {
	
	public ProductNotFoundException(String productCode) {
		super("Producto no encontrado en inventario: " + productCode);
	}
}