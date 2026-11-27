package com.example.demo.domain.port.out;

import com.example.demo.domain.model.PedidoDTO;

public interface PedidoRepositoryPort {
	
	void guardar(PedidoDTO pedido, String tipoEvento);
}
