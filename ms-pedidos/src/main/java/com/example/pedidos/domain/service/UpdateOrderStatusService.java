package com.example.pedidos.domain.service;

import java.util.UUID;
import com.example.pedidos.domain.model.Order;
import com.example.pedidos.domain.port.in.UpdateOrderStatusUseCase;
import com.example.pedidos.domain.port.out.OrderRepositoryPort;

public class UpdateOrderStatusService implements UpdateOrderStatusUseCase {

	private final OrderRepositoryPort orderRepositoryPort;

	public UpdateOrderStatusService(OrderRepositoryPort orderRepositoryPort) {
		this.orderRepositoryPort = orderRepositoryPort;
	}

	@Override
	public void updateStatus(UUID orderId, String newStatus) {
		Order order = orderRepositoryPort.findById(orderId)
				.orElseThrow(() -> new RuntimeException("Pedido no encontrado: " + orderId));

		if ("CONFIRMED".equals(newStatus)) {
			order.confirm();
		} else if ("CANCELLED".equals(newStatus)) {
			order.cancel(); // Ajusta al método que tengas en Order para cambiar a CANCELLED
		}

		orderRepositoryPort.save(order);
	}
}
