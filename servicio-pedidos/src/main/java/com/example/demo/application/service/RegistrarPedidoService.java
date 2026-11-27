package com.example.demo.application.service;

import com.example.demo.domain.model.PedidoDTO;
import com.example.demo.domain.port.in.RegistrarPedidoUseCase;
import com.example.demo.domain.port.out.PedidoRepositoryPort;

public class RegistrarPedidoService implements RegistrarPedidoUseCase {

	private final PedidoRepositoryPort pedidoRepositoryPort;

	public RegistrarPedidoService(PedidoRepositoryPort pedidoRepositoryPort) {
		this.pedidoRepositoryPort = pedidoRepositoryPort;
	}

	@Override
	public void registrarPedido(PedidoDTO pedido, String tipoEvento) {
		// Aquí irían tus validaciones o reglas de negocio puras (ej. cantidad > 0)
		pedidoRepositoryPort.guardar(pedido, tipoEvento);
	}
		
}