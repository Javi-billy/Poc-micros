package com.example.pedidos.domain.exception;

import java.util.UUID;

public class OrderNotFoundException extends RuntimeException {

	public OrderNotFoundException(UUID id) {
		super("Pedido no encontrado: " + id);
	}
}
