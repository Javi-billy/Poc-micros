package com.example.demo.application.service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.example.demo.domain.model.PedidoDTO;
import com.example.demo.domain.port.in.ProcesarPedidoUseCase;
import com.example.demo.domain.port.out.InventarioPort;

public class ProcesarPedidoService implements ProcesarPedidoUseCase {

	private final InventarioPort inventarioPort;
	private final Set<String> pedidosProcesados = ConcurrentHashMap.newKeySet();

	public ProcesarPedidoService(InventarioPort inventarioPort) {
		this.inventarioPort = inventarioPort;
	}

	@Override
	public boolean procesar(PedidoDTO pedido) {
		if (pedidosProcesados.contains(pedido.id())) {
			System.out.println("IDEMPOTENCIA -> El pedido " + pedido.id() + " ya fue procesado antes. Ignorando duplicado.");
			return true; // Devolvemos true para que Kafka haga ack y avance la cola
		}

		if ("FALLO-RED".equals(pedido.id())) {
			System.err.println("ERROR DE RED -> Simulando fallo sin confirmar offset.");
			return false; // Devolvemos false para no hacer ack
		}

		inventarioPort.descontarStock(pedido.producto(), pedido.cantidad());
		System.out.println("STOCK ACTUALIZADO -> Descontadas " + pedido.cantidad() + " unidades de " + pedido.producto());

		pedidosProcesados.add(pedido.id());
		System.out.println("ÉXITO -> Notificación enviada para pedido: " + pedido.id());
		return true;
	}
}
