package com.example.pedidos.infrastructure.adapter.out.feign.kafka;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "servicio-pedidos", url = "${clients.servicio-pedidos.url}")
interface KafkaProducerFeignClient {

	@PostMapping("/pedidos")
	void sendOrderEvent(@RequestBody PedidoAvroRequest payload);

	@PutMapping("/pedidos/cancelacion")
	void sendOrderCancelEvent(@RequestBody PedidoAvroRequest payload);

	record PedidoAvroRequest(String id, String producto, int cantidad) {
	}

}