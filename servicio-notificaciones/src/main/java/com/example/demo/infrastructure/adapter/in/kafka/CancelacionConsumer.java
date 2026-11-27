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

import com.example.demo.avro.PedidoCanceladoAvro;
import com.example.demo.domain.model.PedidoDTO;
import com.example.demo.domain.port.in.ProcesarCancelacionUseCase;

@Component
public class CancelacionConsumer {

	@Value("${server.port}")
	private String puerto;

	private final ProcesarCancelacionUseCase procesarCancelacionUseCase;

	public CancelacionConsumer(ProcesarCancelacionUseCase procesarCancelacionUseCase) {
		this.procesarCancelacionUseCase = procesarCancelacionUseCase;
	}

	@RetryableTopic(attempts = "3", backOff = @BackOff(delay = 1000, multiplier = 2.0), dltStrategy = DltStrategy.FAIL_ON_ERROR)
	@KafkaListener(topics = "${kafka.topic.pedidos-cancelados}", groupId = "grupo-cancelaciones")
	public void procesarCancelacion(PedidoCanceladoAvro cancelacionAvro,
			@Header(KafkaHeaders.RECEIVED_PARTITION) int particion,
			@Header(KafkaHeaders.OFFSET) long offset,
			Acknowledgment ack) {

		String pedidoIdStr = cancelacionAvro.getId().toString();
		System.out.println("[Instancia puerto " + puerto + "] CANCELACIÓN -> Partición: " + particion
				+ " | Offset: " + offset + " | Pedido: " + pedidoIdStr);

		// 1. Mapeamos de Avro a DTO de dominio
		PedidoDTO pedido = new PedidoDTO(
				pedidoIdStr,
				cancelacionAvro.getProducto().toString(),
				cancelacionAvro.getCantidad()
		);

		// 2. Ejecutamos el caso de uso (idempotencia + incremento de stock en ms-inventario)
		boolean procesadoConExito = procesarCancelacionUseCase.procesarCancelacion(pedido);

		// 3. Confirmación manual del offset
		if (procesadoConExito) {
			ack.acknowledge();
			System.out.println("COMMIT MANUAL CANCELACIÓN -> Offset " + offset + " confirmado en Kafka.");
		}
	}

	@DltHandler
	public void manejarErrorEnDlt(PedidoCanceladoAvro cancelacion, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
		System.err.println("[Instancia puerto " + puerto + "] ALERTA DLT CANCELACIÓN -> Enviado a " + topic
				+ " | Pedido: " + cancelacion.getId().toString());
	}
}
