package com.example.demo.infrastructure.adapter.in.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.domain.model.PedidoDTO;
import com.example.demo.domain.port.in.RegistrarPedidoUseCase;

@RestController
@RequestMapping("/pedidos")
public class PedidosController {

	private final RegistrarPedidoUseCase registrarPedidoUseCase;

	public PedidosController(RegistrarPedidoUseCase registrarPedidoUseCase) {
		this.registrarPedidoUseCase = registrarPedidoUseCase;
	}

	/**
	 * Recibir peticiones HTTP y publicar mensajes directamente en los tópicos de
	 * Kafka para que los consuman tus instancias de servicio-notificaciones
	 * 
	 * @param request
	 * @return ResponseEntity<String>
	 */
	@PostMapping
	public ResponseEntity<String> crearPedido(@RequestBody PedidoDTO request) {
		PedidoDTO pedido = new PedidoDTO(request.id(), request.producto(), request.cantidad());
		registrarPedidoUseCase.registrarPedido(pedido, "CREATED");

		return ResponseEntity.status(HttpStatus.CREATED)
				.body("Creacion Pedido registrado en BD de forma segura: " + pedido.id());
	}
	
	/**
	 * Recibir peticiones HTTP y publicar mensajes directamente en los tópicos de
	 * Kafka para que los consuman tus instancias de servicio-notificaciones
	 * 
	 * @param request
	 * @return ResponseEntity<String>
	 */
	@PutMapping("/cancelacion")
	public ResponseEntity<String> cancelarPedido(@RequestBody PedidoDTO request) {
		PedidoDTO pedido = new PedidoDTO(request.id(), request.producto(), request.cantidad());
		registrarPedidoUseCase.registrarPedido(pedido, "CANCELLED");
		return ResponseEntity.status(HttpStatus.CREATED)
				.body("Cancelacion Pedido registrado en BD de forma segura: " + pedido.id());
	}
}
