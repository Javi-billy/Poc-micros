package com.example.pedidos.infrastructure.adapter.in.messaging;

import java.util.UUID;

import org.springframework.kafka.annotation.KafkaListener;

import org.springframework.stereotype.Component;

import com.example.pedidos.domain.port.in.UpdateOrderStatusUseCase;

@Component
public class OrderSagaResponseListener {

	private final UpdateOrderStatusUseCase updateOrderStatusUseCase;

	public OrderSagaResponseListener(UpdateOrderStatusUseCase updateOrderStatusUseCase) {
		this.updateOrderStatusUseCase = updateOrderStatusUseCase;
	}

	@KafkaListener(topics = "order-saga-responses", groupId = "pedidos-saga-group")
	public void onSagaResponse(OrderSagaResponseEvent event) {
		if ("CONFIRMED".equals(event.status())) {
			updateOrderStatusUseCase.updateStatus(event.orderId(), "CONFIRMED");
		} else if ("CANCELLED".equals(event.status())) {
			updateOrderStatusUseCase.updateStatus(event.orderId(), "CANCELLED");
		}
	}

	public record OrderSagaResponseEvent(UUID orderId, String status, String reason) {
	}
}