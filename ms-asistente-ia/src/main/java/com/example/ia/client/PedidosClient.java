package com.example.ia.client;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "ms-pedidos", url = "${clients.pedidos.url}")
public interface PedidosClient {

	@PostMapping("/api/orders")
	OrderResponse crearPedido(@RequestBody CreateOrderRequest request);

	@GetMapping("/api/orders/{orderId}")
	OrderResponse consultarPedido(@PathVariable("orderId") UUID orderId);

	@GetMapping("/api/orders")
	List<OrderResponse> listarPedidos();

	@PutMapping("/api/orders/{orderId}/cancel")
	OrderResponse cancelarPedido(@PathVariable("orderId") UUID orderId);

	record CreateOrderRequest(String productCode, int quantity, BigDecimal totalAmount, String customerEmail) {
	}

	record OrderResponse(UUID orderId, String status, String productCode, int quantity) {
	}
}
