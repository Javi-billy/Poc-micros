package com.example.demo.infrastructure.adapter.out.persistence;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.domain.model.PedidoDTO;
import com.example.demo.infrastructure.adapter.out.kafka.PedidoCanceladoProducer;
import com.example.demo.infrastructure.adapter.out.kafka.PedidoProducer;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class OutboxRelay {

	private final OutboxRepository outboxRepository;
	private final PedidoProducer pedidoProducer;
	private final PedidoCanceladoProducer pedidoCanceladoProducer;
	private final ObjectMapper objectMapper;

	public OutboxRelay(OutboxRepository outboxRepository, PedidoProducer pedidoProducer,
			PedidoCanceladoProducer pedidoCanceladoProducer, ObjectMapper objectMapper) {
		this.outboxRepository = outboxRepository;
		this.pedidoProducer = pedidoProducer;
		this.pedidoCanceladoProducer = pedidoCanceladoProducer;
		this.objectMapper = objectMapper;
	}

	@Scheduled(fixedDelay = 3000) // Se ejecuta cada 3 segundos
	@Transactional
	public void publicarEventosPendientes() {
		List<OutboxEvent> pendientes = outboxRepository.findByEnviadoFalse();
		for (OutboxEvent evento : pendientes) {
			try {
				PedidoDTO pedidoDto = objectMapper.readValue(evento.getPayload(), PedidoDTO.class);
				if ("CREATED".equals(evento.getType())) {
					pedidoProducer.enviarPedido(pedidoDto);
				} else if ("CANCELLED".equals(evento.getType())) {
					pedidoCanceladoProducer.enviarPedido(pedidoDto);
				}
				evento.setEnviado(true);
				outboxRepository.save(evento);
				System.out
						.println("OUTBOX RELAY -> Evento enviado a Kafka y marcado como procesado: " + pedidoDto.id());
			} catch (Exception e) {
				System.err.println(
						"Error al procesar el evento Outbox con ID: " + evento.getId() + ". Error: " + e.getMessage());
			}
		}
	}
}