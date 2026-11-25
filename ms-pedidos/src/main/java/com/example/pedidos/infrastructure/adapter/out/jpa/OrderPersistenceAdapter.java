package com.example.pedidos.infrastructure.adapter.out.jpa;

import org.springframework.stereotype.Component;

import com.example.pedidos.domain.model.Order;
import com.example.pedidos.domain.port.out.OrderRepositoryPort;

@Component
public class OrderPersistenceAdapter implements OrderRepositoryPort {

	private final SpringDataOrderRepository repository;

	public OrderPersistenceAdapter(SpringDataOrderRepository repository) {
		this.repository = repository;
	}

	@Override
	public Order save(Order order) {
		OrderJpaEntity entity = new OrderJpaEntity();
		entity.setId(order.getId());
		entity.setProductCode(order.getProductCode());
		entity.setQuantity(order.getQuantity());
		entity.setTotalAmount(order.getTotalAmount());
		entity.setCustomerEmail(order.getCustomerEmail());
		entity.setStatus(order.getStatus());

		OrderJpaEntity saved = repository.save(entity);
		return new Order(saved.getId(), saved.getProductCode(), saved.getQuantity(), saved.getTotalAmount(),
				saved.getCustomerEmail(), saved.getStatus());
	}
}