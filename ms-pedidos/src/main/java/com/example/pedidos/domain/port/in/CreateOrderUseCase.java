package com.example.pedidos.domain.port.in;

import com.example.pedidos.domain.model.CreateOrderCommand;
import com.example.pedidos.domain.model.Order;

public interface CreateOrderUseCase {

	Order createOrder(CreateOrderCommand command);
		
}