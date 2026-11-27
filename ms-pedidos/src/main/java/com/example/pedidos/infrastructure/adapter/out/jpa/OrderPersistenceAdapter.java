package com.example.pedidos.infrastructure.adapter.out.jpa;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

	@Override
	public Optional<Order> findById(UUID id) {
		return repository.findById(id).map(entity -> new Order(entity.getId(), entity.getProductCode(),
				entity.getQuantity(), entity.getTotalAmount(), entity.getCustomerEmail(), entity.getStatus()));
	}

	@Override
	public List<Optional<Order>> list() {
		List<OrderJpaEntity> orders = repository.findAll();
		return orders
				.stream().map(entity -> Optional.of(new Order(entity.getId(), entity.getProductCode(),
						entity.getQuantity(), entity.getTotalAmount(), entity.getCustomerEmail(), entity.getStatus())))
				.toList();
	}

}