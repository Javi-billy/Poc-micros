package com.example.pedidos.domain.port.in;

import java.util.UUID;

import com.example.pedidos.domain.model.Order;

public interface CancelOrderUseCase {

	Order cancelOrder(UUID orderId);

}