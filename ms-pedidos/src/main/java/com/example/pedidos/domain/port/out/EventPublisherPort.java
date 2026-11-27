package com.example.pedidos.domain.port.out;

import com.example.pedidos.domain.model.Order;

public interface EventPublisherPort {

	void publishOrderCreated(Order order);

	void publishOrderCancelled(Order order);
}
