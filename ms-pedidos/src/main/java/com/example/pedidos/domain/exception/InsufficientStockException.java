package com.example.pedidos.domain.exception;

public class InsufficientStockException extends RuntimeException {
	public InsufficientStockException(String productCode) {
		super("Stock insuficiente para el producto: " + productCode);
	}
}