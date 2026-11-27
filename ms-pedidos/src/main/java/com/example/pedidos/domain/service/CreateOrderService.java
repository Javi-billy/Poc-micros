package com.example.pedidos.domain.service;

import java.util.UUID;

import com.example.pedidos.domain.exception.InsufficientStockException;
import com.example.pedidos.domain.model.CreateOrderCommand;
import com.example.pedidos.domain.model.Order;
import com.example.pedidos.domain.port.in.CreateOrderUseCase;
import com.example.pedidos.domain.port.out.EventPublisherPort;
import com.example.pedidos.domain.port.out.InventoryPort;
import com.example.pedidos.domain.port.out.OrderRepositoryPort;

public class CreateOrderService implements CreateOrderUseCase {

	private final InventoryPort inventoryPort;
	private final OrderRepositoryPort orderRepositoryPort;
	private final EventPublisherPort eventPublisherPort;

	public CreateOrderService(InventoryPort inventoryPort, OrderRepositoryPort orderRepositoryPort,
			EventPublisherPort eventPublisherPort) {
		this.inventoryPort = inventoryPort;
		this.orderRepositoryPort = orderRepositoryPort;
		this.eventPublisherPort = eventPublisherPort;
	}

	@Override
	public Order createOrder(CreateOrderCommand command) {
		if (!inventoryPort.hasStock(command.productCode(), command.quantity())) {
			throw new InsufficientStockException(command.productCode());
		}

		Order order = new Order(UUID.randomUUID(), command.productCode(), command.quantity(), command.totalAmount(),
				command.customerEmail(), "PENDING");
		//order.confirm();

		Order savedOrder = orderRepositoryPort.save(order);
		eventPublisherPort.publishOrderCreated(savedOrder);

		return savedOrder;
	}
}
