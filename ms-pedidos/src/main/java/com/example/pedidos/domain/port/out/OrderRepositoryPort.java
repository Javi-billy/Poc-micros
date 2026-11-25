package com.example.pedidos.domain.port.out;

import com.example.pedidos.domain.model.Order;

public interface OrderRepositoryPort {
	Order save(Order order);
}
