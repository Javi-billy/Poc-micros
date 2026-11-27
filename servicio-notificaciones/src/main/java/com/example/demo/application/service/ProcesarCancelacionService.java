package com.example.demo.application.service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.example.demo.domain.model.PedidoDTO;
import com.example.demo.domain.port.in.ProcesarCancelacionUseCase;
import com.example.demo.domain.port.out.InventarioPort;

public class ProcesarCancelacionService implements ProcesarCancelacionUseCase {

	private final InventarioPort inventarioPort;
	private final Set<String> pedidosProcesados = ConcurrentHashMap.newKeySet();

	public ProcesarCancelacionService(InventarioPort inventarioPort) {
		this.inventarioPort = inventarioPort;
	}

	@Override
	public boolean procesarCancelacion(PedidoDTO pedido) {
		if (pedidosProcesados.contains(pedido.id())) {
			System.out.println(
					"IDEMPOTENCIA -> El pedido " + pedido.id() + " ya fue procesado antes. Ignorando duplicado.");
			return true; // Devolvemos true para que Kafka haga ack y avance la cola
		}

		if ("FALLO-RED".equals(pedido.id())) {
			System.err.println("ERROR DE RED -> Simulando fallo sin confirmar offset.");
			return false; // Devolvemos false para no hacer ack
		}

		inventarioPort.incrementarStock(pedido.producto(), pedido.cantidad());
		System.out.println(
				"STOCK ACTUALIZADO -> Incrementadas " + pedido.cantidad() + " unidades de " + pedido.producto());

		pedidosProcesados.add(pedido.id());
		System.out.println("ÉXITO -> Notificación enviada para cancelacion de pedido: " + pedido.id());
		return true;
	}
}
