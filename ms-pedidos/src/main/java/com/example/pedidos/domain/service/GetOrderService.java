package com.example.pedidos.domain.service;

import java.util.List;
import java.util.UUID;
import com.example.pedidos.domain.model.Order;
import com.example.pedidos.domain.port.in.GetOrderUseCase;
import com.example.pedidos.domain.port.out.OrderRepositoryPort;

public class GetOrderService implements GetOrderUseCase {

	private final OrderRepositoryPort orderRepositoryPort;

	public GetOrderService(OrderRepositoryPort orderRepositoryPort) {
		this.orderRepositoryPort = orderRepositoryPort;
	}

	@Override
	public Order getOrderById(UUID orderId) {
		return orderRepositoryPort.findById(orderId)
				.orElseThrow(() -> new RuntimeException("Pedido no encontrado: " + orderId));
	}

	@Override
	public List<Order> getOrders() {
		return orderRepositoryPort.list().stream()
				.map(optionalOrder -> optionalOrder.orElseThrow(() -> new RuntimeException("Pedido no encontrado")))
				.toList();
	}
}