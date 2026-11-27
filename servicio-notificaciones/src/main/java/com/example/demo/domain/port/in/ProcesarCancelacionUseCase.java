package com.example.demo.domain.port.in;

import com.example.demo.domain.model.PedidoDTO;

public interface ProcesarCancelacionUseCase {

	boolean procesarCancelacion(PedidoDTO pedido);
}
