package com.example.demo.domain.port.in;

import com.example.demo.domain.model.PedidoDTO;

public interface RegistrarPedidoUseCase {

	void registrarPedido(PedidoDTO pedido, String tipoEvento);
	
}
