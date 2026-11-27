package com.example.pedidos.infrastructure.adapter.in.web;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.http.HttpStatus;

import com.example.pedidos.domain.model.CreateOrderCommand;
import com.example.pedidos.domain.model.Order;
import com.example.pedidos.domain.port.in.CancelOrderUseCase;
import com.example.pedidos.domain.port.in.CreateOrderUseCase;
import com.example.pedidos.domain.port.in.GetOrderUseCase;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/orders")
@Tag(name = "Pedidos", description = "Creación y consulta de órdenes de compra (Patrón Saga)")
public class OrderController {

	private final CreateOrderUseCase createOrderUseCase;
	private final CancelOrderUseCase cancelOrderUseCase;
	private final GetOrderUseCase getOrderUseCase;

	public OrderController(CreateOrderUseCase createOrderUseCase, CancelOrderUseCase cancelOrderUseCase,
			GetOrderUseCase getOrderUseCase) {
		this.createOrderUseCase = createOrderUseCase;
		this.cancelOrderUseCase = cancelOrderUseCase;
		this.getOrderUseCase = getOrderUseCase;
	}

	/**
	 * @param request
	 * @return OrderResponse
	 */
	@Operation(summary = "Crear un nuevo pedido", description = "Valida el stock en ms-inventario, registra la orden y dispara el evento hacia Kafka.")
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public OrderResponse createOrder(@RequestBody CreateOrderRequest request) {
		CreateOrderCommand command = new CreateOrderCommand(request.productCode(), request.quantity(),
				request.totalAmount(), request.customerEmail());
		Order order = createOrderUseCase.createOrder(command);
		return toResponse(order);
	}

	@Operation(summary = "Consultar estado del pedido", description = "Permite comprobar si la Saga ha confirmado (CONFIRMED) o cancelado (CANCELLED) el pedido.")
	@GetMapping("/{orderId}")
	public OrderResponse getOrder(@PathVariable UUID orderId) {
		Order order = getOrderUseCase.getOrderById(orderId);
		return toResponse(order);
	}

	@Operation(summary = "Listado de pedidos", description = "Permite recuperar el listado de todos los pedidos.")
	@GetMapping
	public List<OrderResponse> listOrder() {
		List<Order> order = getOrderUseCase.getOrders();
		return order.stream().map(o -> new OrderResponse(o.getId(), o.getStatus(), o.getProductCode(), o.getQuantity()))
				.toList();
	}

	@Operation(summary = "Anulacion de un pedido", description = "Permite anular un pedido creado anteriormente.")
	@PutMapping("/{orderId}")
	public OrderResponse cancelOrder(@PathVariable UUID orderId) {
		Order cancelledOrder = cancelOrderUseCase.cancelOrder(orderId);
		return toResponse(cancelledOrder);
	}

	public record CreateOrderRequest(String productCode, int quantity, BigDecimal totalAmount, String customerEmail) {
	}

	public record OrderResponse(UUID orderId, String status, String productCode, int quantity) {
	}

	private OrderResponse toResponse(Order order) {
		return new OrderResponse(order.getId(), order.getStatus(), order.getProductCode(), order.getQuantity());
	}
}
