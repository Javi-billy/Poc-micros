package com.example.demo.infrastructure.adapter.out.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.example.demo.avro.PedidoCanceladoAvro;
import com.example.demo.domain.model.PedidoDTO;

@Service
public class PedidoCanceladoProducer {

	private static final String TOPIC_PEDIDOS_CANCELADOS = "pedidos-cancelados-avro-topic";

	private final KafkaTemplate<String, PedidoCanceladoAvro> kafkaTemplate;

	public PedidoCanceladoProducer(KafkaTemplate<String, PedidoCanceladoAvro> kafkaTemplate) {
		this.kafkaTemplate = kafkaTemplate;
	}

	public void enviarPedido(PedidoDTO dto) {
		PedidoCanceladoAvro eventoAvro = PedidoCanceladoAvro.newBuilder().setId(dto.id()).setProducto(dto.producto())
				.setCantidad(dto.cantidad()).build();
		kafkaTemplate.send(TOPIC_PEDIDOS_CANCELADOS, eventoAvro.getId().toString(), eventoAvro);
	}

}
