package com.example.demo.infrastructure.adapter.out.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.example.demo.avro.PedidoAvro;
import com.example.demo.domain.model.PedidoDTO;

@Service
public class PedidoProducer {

	private static final String TOPIC_PEDIDOS = "pedidos-avro-topic";

	private final KafkaTemplate<String, PedidoAvro> kafkaTemplate;

	public PedidoProducer(KafkaTemplate<String, PedidoAvro> kafkaTemplate) {
		this.kafkaTemplate = kafkaTemplate;
	}

	public void enviarPedido(PedidoDTO dto) {
		PedidoAvro eventoAvro = PedidoAvro.newBuilder().setId(dto.id()).setProducto(dto.producto())
				.setCantidad(dto.cantidad()).build();
		kafkaTemplate.send(TOPIC_PEDIDOS, eventoAvro.getId().toString(), eventoAvro);
	}

}
