package com.example.demo.infrastructure.adapter.out.persistence;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.domain.model.PedidoDTO;
import com.example.demo.domain.port.out.PedidoRepositoryPort;


@Component
public class PedidoPersistenceAdapter implements PedidoRepositoryPort {

	private final OutboxRepository outboxRepository;
	// private final PedidoJpaRepository pedidoJpaRepository; (cuando lo añadas)

	public PedidoPersistenceAdapter(OutboxRepository outboxRepository) {
		this.outboxRepository = outboxRepository;
	}

	@Override
	@Transactional
	public void guardar(PedidoDTO pedido) {
		// 1. Aquí guardarías la entidad JPA del pedido real en pedidoJpaRepository

		// 2. Guardas el evento Outbox en la misma transacción
		OutboxEvent evento = new OutboxEvent();
		evento.setPedidoId(pedido.id());
		evento.setProducto(pedido.producto());
		evento.setCantidad(pedido.cantidad());
		outboxRepository.save(evento);

		System.out.println("BD -> Pedido y OutboxEvent guardados atómicamente: " + pedido.id());
	}
}