package com.example.inventario.domain.model;

import com.example.inventario.domain.exception.InsufficientStockException;

public class ProductStock {

	private final String productCode;
	private int availableQuantity;

	public ProductStock(String productCode, int availableQuantity) {
		if (productCode == null || productCode.isBlank()) {
			throw new IllegalArgumentException("El código de producto es obligatorio");
		}
		if (availableQuantity < 0) {
			throw new IllegalArgumentException("El stock inicial no puede ser negativo");
		}
		this.productCode = productCode;
		this.availableQuantity = availableQuantity;
	}

	public boolean hasEnoughStock(int requestedQuantity) {
		return requestedQuantity > 0 && this.availableQuantity >= requestedQuantity;
	}

	public void decreaseStock(int quantityToDecrease) {
		if (!hasEnoughStock(quantityToDecrease)) {
			throw new InsufficientStockException(this.productCode);
		}
		this.availableQuantity -= quantityToDecrease;
	}

	public void increaseStock(int quantityToAdd) {
		if (quantityToAdd <= 0) {
			throw new IllegalArgumentException("La cantidad a incrementar debe ser mayor que cero");
		}
		this.availableQuantity += quantityToAdd;
	}

	public String getProductCode() {
		return productCode;
	}

	public int getAvailableQuantity() {
		return availableQuantity;
	}
}
