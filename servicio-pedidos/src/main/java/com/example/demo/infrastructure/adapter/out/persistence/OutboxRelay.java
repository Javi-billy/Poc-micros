package com.example.demo.infrastructure.adapter.out.persistence;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.domain.model.PedidoDTO;
import com.example.demo.infrastructure.adapter.out.kafka.PedidoProducer;

@Component
public class OutboxRelay {

	private final OutboxRepository outboxRepository;
	private final PedidoProducer pedidoProducer;

	public OutboxRelay(OutboxRepository outboxRepository, PedidoProducer pedidoProducer) {
		this.outboxRepository = outboxRepository;
		this.pedidoProducer = pedidoProducer;
	}

	@Scheduled(fixedDelay = 3000) // Se ejecuta cada 3 segundos
	@Transactional
	public void publicarEventosPendientes() {
		List<OutboxEvent> pendientes = outboxRepository.findByEnviadoFalse();

		for (OutboxEvent evento : pendientes) {
			PedidoDTO dto = new PedidoDTO(evento.getPedidoId(), evento.getProducto(), evento.getCantidad());
			pedidoProducer.enviarPedido(dto); // Envía a Kafka (Avro)

			evento.setEnviado(true); // Marca como enviado en BD
			outboxRepository.save(evento);
			System.out.println("OUTBOX RELAY -> Evento enviado a Kafka y marcado como procesado: " + dto.id());
		}
	}
}
