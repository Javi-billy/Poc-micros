package com.example.demo.infrastructure.adapter.in.kafka;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import com.example.demo.avro.PedidoAvro;
import com.example.demo.domain.port.out.InventarioPort;

import io.confluent.kafka.serializers.KafkaAvroSerializer;

@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = { "${kafka.topic.pedidos}", "${kafka.topic.pedidos-cancelados}" })
@TestPropertySource(properties = { "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
		"spring.kafka.consumer.auto-offset-reset=earliest", "kafka.topic.pedidos=pedidos-avro-topic",
		"schema.registry.url=mock://test-registry",
		"spring.kafka.properties.schema.registry.url=mock://test-registry" })
class NotificacionConsumerTest {

	@TestConfiguration
	static class AvroTestProducerConfig {

		@Value("${spring.kafka.bootstrap-servers}")
		private String bootstrapServers;

		@Bean("avroTestKafkaTemplate")
		KafkaTemplate<String, PedidoAvro> avroTestKafkaTemplate() {
			Map<String, Object> props = new HashMap<>();
			props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
			props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
			props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
			props.put("schema.registry.url", "mock://test-registry");
			return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(props));
		}
	}

	@Autowired
	@Qualifier("avroTestKafkaTemplate")
	private KafkaTemplate<String, PedidoAvro> kafkaTemplate;

	@Autowired
	private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

	@MockitoSpyBean
	private NotificacionConsumer consumer;

	@MockitoBean
	private InventarioPort inventarioPort;

	@BeforeEach
	void esperarAsignacionParticiones() {
		for (MessageListenerContainer container : kafkaListenerEndpointRegistry.getListenerContainers()) {
			String groupId = container.getGroupId();
			// Esperamos solo al listener principal de pedidos (ni retry, ni dlt, ni
			// cancelaciones)
			if (groupId != null && !groupId.contains("retry") && !groupId.contains("dlt")
					&& !groupId.contains("cancelaciones")) {
				ContainerTestUtils.waitForAssignment(container, 1);
			}
		}
	}

	@Test
	void cuandoLlegaPedidoValido_entoncesConsumerLoProcesaYDescuentaStock() throws Exception {
		CountDownLatch latch = new CountDownLatch(1);

		doAnswer(invocation -> {
			invocation.callRealMethod();
			latch.countDown();
			return null;
		}).when(consumer).procesarPedido(any(PedidoAvro.class), anyInt(), anyLong(), any(Acknowledgment.class));

		String pedidoId = UUID.randomUUID().toString();
		PedidoAvro pedidoPrueba = PedidoAvro.newBuilder().setId(pedidoId).setProducto("Ratón inalámbrico")
				.setCantidad(4).build();

		// Añadimos .get() para confirmar la entrega en EmbeddedKafka antes de esperar
		kafkaTemplate.send("pedidos-avro-topic", pedidoId, pedidoPrueba).get(5, TimeUnit.SECONDS);

		boolean mensajeRecibido = latch.await(10, TimeUnit.SECONDS);
		assertTrue(mensajeRecibido, "El consumidor debería haber procesado el pedido");

		verify(inventarioPort, times(1)).descontarStock("Ratón inalámbrico", 4);
	}

	@Test
	void cuandoLlegaPedidoDuplicado_entoncesSeInvocaDosVecesPeroEsIdempotente() throws Exception {
		CountDownLatch latch = new CountDownLatch(2);

		doAnswer(invocation -> {
			invocation.callRealMethod();
			latch.countDown();
			return null;
		}).when(consumer).procesarPedido(any(PedidoAvro.class), anyInt(), anyLong(), any(Acknowledgment.class));

		String pedidoIdDuplicado = UUID.randomUUID().toString();
		PedidoAvro pedidoDuplicado = PedidoAvro.newBuilder().setId(pedidoIdDuplicado).setProducto("Teclado RGB")
				.setCantidad(7).build();

		kafkaTemplate.send("pedidos-avro-topic", pedidoIdDuplicado, pedidoDuplicado);
		kafkaTemplate.send("pedidos-avro-topic", pedidoIdDuplicado, pedidoDuplicado);

		boolean ambosRecibidos = latch.await(15, TimeUnit.SECONDS);
		assertTrue(ambosRecibidos, "Ambos mensajes deberían haber sido leídos por el listener");

		verify(consumer, times(2)).procesarPedido(any(PedidoAvro.class), anyInt(), anyLong(),
				any(Acknowledgment.class));

		verify(inventarioPort, times(1)).descontarStock("Teclado RGB", 7);
	}
}
