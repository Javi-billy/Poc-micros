package com.example.demo.infrastructure.adapter.out.persistence;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.domain.model.PedidoDTO;
import com.example.demo.domain.port.out.PedidoRepositoryPort;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class PedidoPersistenceAdapter implements PedidoRepositoryPort {

	private final OutboxRepository outboxRepository;
	private final ObjectMapper objectMapper;
	// private final PedidoJpaRepository pedidoJpaRepository; (cuando lo añadas)

	public PedidoPersistenceAdapter(OutboxRepository outboxRepository, ObjectMapper objectMapper) {
		this.outboxRepository = outboxRepository;
		this.objectMapper = objectMapper;
	}

	@Override
	@Transactional
	public void guardar(PedidoDTO pedido, String tipoEvento) {
		try {
			// 1. Aquí guardarías la entidad JPA del pedido real en pedidoJpaRepository

			// 2. Guardas el evento Outbox en la misma transacción
			OutboxEvent evento = new OutboxEvent();
			evento.setAggregateId(pedido.id());
			evento.setPayload(objectMapper.writeValueAsString(pedido));
			evento.setType(tipoEvento); // "CREATED" o "CANCELLED"
			outboxRepository.save(evento);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Error serializando PedidoDTO para Outbox", e);
		}
		System.out.println("BD -> Pedido y OutboxEvent guardados atómicamente: " + pedido.id());
	}
}