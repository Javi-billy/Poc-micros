package com.example.pedidos.domain.port.in;

import java.util.List;
import java.util.UUID;
import com.example.pedidos.domain.model.Order;

public interface GetOrderUseCase {

	Order getOrderById(UUID orderId);
	
	List<Order> getOrders();
}
