package com.example.inventario.domain.exception;

public class ProductAlreadyExistsException extends RuntimeException {

	public ProductAlreadyExistsException(String productCode) {
		super("El producto ya existe en el inventario: " + productCode);
	}
}