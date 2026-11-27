package com.example.pedidos.domain.port.in;

import java.util.UUID;

public interface UpdateOrderStatusUseCase {
	void updateStatus(UUID orderId, String newStatus);
}