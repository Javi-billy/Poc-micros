package com.example.pedidos.domain.model;

import java.math.BigDecimal;
import java.util.UUID;

public class Order {

	private final UUID id;
	private final String productCode;
	private final int quantity;
	private final BigDecimal totalAmount;
	private final String customerEmail;
	private String status;

	public Order(UUID id, String productCode, int quantity, BigDecimal totalAmount, String customerEmail,
			String status) {
		if (quantity <= 0) {
			throw new IllegalArgumentException("La cantidad debe ser mayor a cero");
		}
		if (totalAmount == null || totalAmount.compareTo(BigDecimal.ZERO) <= 0) {
			throw new IllegalArgumentException("El importe total debe ser mayor a cero");
		}
		this.id = id;
		this.productCode = productCode;
		this.quantity = quantity;
		this.totalAmount = totalAmount;
		this.customerEmail = customerEmail;
		this.status = status;
	}

	public void confirm() {
		this.status = "CONFIRMED";
	}

	public UUID getId() {
		return id;
	}

	public String getProductCode() {
		return productCode;
	}

	public int getQuantity() {
		return quantity;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public String getCustomerEmail() {
		return customerEmail;
	}

	public String getStatus() {
		return status;
	}
}
