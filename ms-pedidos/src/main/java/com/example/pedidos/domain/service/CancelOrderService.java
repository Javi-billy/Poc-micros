package com.example.pedidos.domain.service;

import java.util.UUID;

import com.example.pedidos.domain.exception.OrderNotFoundException;
import com.example.pedidos.domain.model.Order;
import com.example.pedidos.domain.port.in.CancelOrderUseCase;
import com.example.pedidos.domain.port.out.EventPublisherPort;
import com.example.pedidos.domain.port.out.OrderRepositoryPort;

public class CancelOrderService implements CancelOrderUseCase {

	private final OrderRepositoryPort orderRepositoryPort;
	private final EventPublisherPort eventPublisherPort;

	public CancelOrderService(OrderRepositoryPort orderRepositoryPort, EventPublisherPort eventPublisherPort) {
		this.orderRepositoryPort = orderRepositoryPort;
		this.eventPublisherPort = eventPublisherPort;
	}

	@Override
	public Order cancelOrder(UUID orderId) {
		Order order = orderRepositoryPort.findById(orderId).orElseThrow(() -> new OrderNotFoundException(orderId));
		order.cancel();

		Order savedOrder = orderRepositoryPort.save(order);
		eventPublisherPort.publishOrderCancelled(savedOrder);

		return savedOrder;
	}
}
