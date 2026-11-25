package com.example.pedidos.infrastructure.adapter.in.web;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.http.HttpStatus;

import com.example.pedidos.domain.model.CreateOrderCommand;
import com.example.pedidos.domain.model.Order;
import com.example.pedidos.domain.port.in.CreateOrderUseCase;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

	private final CreateOrderUseCase createOrderUseCase;

	public OrderController(CreateOrderUseCase createOrderUseCase) {
		this.createOrderUseCase = createOrderUseCase;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public OrderResponse createOrder(@RequestBody CreateOrderRequest request) {
		CreateOrderCommand command = new CreateOrderCommand(request.productCode(), request.quantity(),
				request.totalAmount(), request.customerEmail());
		Order order = createOrderUseCase.createOrder(command);
		return new OrderResponse(order.getId(), order.getStatus());
	}

	public record CreateOrderRequest(String productCode, int quantity, BigDecimal totalAmount, String customerEmail) {
	}

	public record OrderResponse(UUID orderId, String status) {
	}
}
