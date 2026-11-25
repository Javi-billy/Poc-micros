package com.example.demo.infrastructure.adapter.in.kafka;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.BackOff;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import com.example.demo.avro.PedidoAvro;
import com.example.demo.domain.model.PedidoDTO;
import com.example.demo.domain.port.in.ProcesarPedidoUseCase;

@Component
public class NotificacionConsumer {

	@Value("${server.port}")
	private String puerto;

	private final ProcesarPedidoUseCase procesarPedidoUseCase;

	public NotificacionConsumer(ProcesarPedidoUseCase procesarPedidoUseCase) {
		this.procesarPedidoUseCase = procesarPedidoUseCase;
	}

	@RetryableTopic(attempts = "3", backOff = @BackOff(delay = 2000, multiplier = 2.0), dltStrategy = DltStrategy.FAIL_ON_ERROR)
	@KafkaListener(topics = "${kafka.topic.pedidos}", groupId = "${spring.kafka.consumer.group-id}")
	public void procesarPedido(PedidoAvro pedidoAvro, @Header(KafkaHeaders.RECEIVED_PARTITION) int particion,
			@Header(KafkaHeaders.OFFSET) long offset, Acknowledgment ack) {
		
		System.out.println("[Instancia puerto " + puerto + "] -> Partición: " + particion + " | Offset: " + offset
				+ " | Pedido: " + pedidoAvro.getId().toString());

		// 1. Mapeamos de Avro (infraestructura) a Pedido (dominio puro)
		PedidoDTO pedido = new PedidoDTO(
				pedidoAvro.getId().toString(),
				pedidoAvro.getProducto().toString(),
				pedidoAvro.getCantidad()
		);

		// 2. Ejecutamos el caso de uso
		boolean procesadoConExito = procesarPedidoUseCase.procesar(pedido);

		// 3. Confirmación manual solo si el caso de uso indica que avanzamos
		if (procesadoConExito) {
			ack.acknowledge();
			System.out.println("COMMIT MANUAL -> Offset " + offset + " confirmado en Kafka.");
		}
	}

	@DltHandler
	public void manejarErrorEnDlt(PedidoAvro pedido, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
		System.err.println("[Instancia puerto " + puerto + "] ALERTA DLT -> Pedido enviado a " + topic + ": "
				+ pedido.getId().toString());
	}
}
