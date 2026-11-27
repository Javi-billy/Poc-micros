package com.example.pedidos.infrastructure.adapter.out.feign.kafka;

import org.springframework.stereotype.Component;

import com.example.pedidos.domain.model.Order;
import com.example.pedidos.domain.port.out.EventPublisherPort;
import com.example.pedidos.infrastructure.adapter.out.feign.ExternalServiceUnavailableException;
import com.example.pedidos.infrastructure.adapter.out.feign.kafka.KafkaProducerFeignClient.PedidoAvroRequest;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@Component
public class KafkaProducerFeignAdapter implements EventPublisherPort {

	private final KafkaProducerFeignClient feignClient;

	public KafkaProducerFeignAdapter(KafkaProducerFeignClient feignClient) {
		this.feignClient = feignClient;
	}

	@Override
	@CircuitBreaker(name = "servicio-pedidos", fallbackMethod = "fallbackPublishOrder")
	public void publishOrderCreated(Order order) {
		PedidoAvroRequest payload = new PedidoAvroRequest(order.getId().toString(), order.getProductCode(),
				order.getQuantity());
		feignClient.sendOrderEvent(payload);
	}

	@Override
	@CircuitBreaker(name = "servicio-pedidos", fallbackMethod = "fallbackPublishOrder")
	public void publishOrderCancelled(Order order) {
		PedidoAvroRequest payload = new PedidoAvroRequest(order.getId().toString(), order.getProductCode(),
				order.getQuantity());
		feignClient.sendOrderCancelEvent(payload);
	}

	@SuppressWarnings("unused")
	private void fallbackPublishOrder(Order order, Throwable t) {
		System.err.println("CIRCUIT BREAKER [servicio-pedidos] -> Fallback activado para pedido " + order.getId()
				+ ". Motivo: " + t.getMessage());
		throw new ExternalServiceUnavailableException(
				"El sistema de mensajería de pedidos no está disponible en este momento.");
	}
}