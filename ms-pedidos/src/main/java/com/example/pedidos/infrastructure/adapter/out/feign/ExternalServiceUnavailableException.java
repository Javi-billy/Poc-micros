package com.example.pedidos.infrastructure.adapter.out.feign;

public class ExternalServiceUnavailableException extends RuntimeException {
	public ExternalServiceUnavailableException(String message) {
		super(message);
	}
}