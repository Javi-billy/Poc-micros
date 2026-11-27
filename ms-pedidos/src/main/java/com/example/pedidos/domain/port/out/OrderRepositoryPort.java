package com.example.pedidos.domain.port.out;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.example.pedidos.domain.model.Order;

public interface OrderRepositoryPort {

	Order save(Order order);

	Optional<Order> findById(UUID id);

	List<Optional<Order>> list();
}
