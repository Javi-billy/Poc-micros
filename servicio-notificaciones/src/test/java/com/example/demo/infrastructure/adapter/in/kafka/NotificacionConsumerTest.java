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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import com.example.demo.avro.PedidoAvro;
import com.example.demo.domain.port.out.InventarioPort;

import io.confluent.kafka.serializers.KafkaAvroSerializer;

@SpringBootTest
@DirtiesContext
@EmbeddedKafka(partitions = 3, topics = { "${kafka.topic.pedidos}" })
@TestPropertySource(properties = { "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
		"spring.kafka.consumer.auto-offset-reset=earliest", "kafka.topic.pedidos=pedidos-avro-topic", 
		"spring.kafka.properties.schema.registry.url=mock://test-registry"})
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

	@MockitoSpyBean
	private NotificacionConsumer consumer;

	@MockitoBean
	private InventarioPort inventarioPort;

	@Test
	void cuandoLlegaPedidoValido_entoncesConsumerLoProcesaYDescuentaStock() throws Exception {
		CountDownLatch latch = new CountDownLatch(1);

		doAnswer(invocation -> {
			invocation.callRealMethod();
			latch.countDown();
			return null;
		}).when(consumer).procesarPedido(any(PedidoAvro.class), anyInt(), anyLong(), any(Acknowledgment.class));

		PedidoAvro pedidoPrueba = PedidoAvro.newBuilder().setId("TEST-01").setProducto("Ratón inalámbrico")
				.setCantidad(4).build();

		kafkaTemplate.send("pedidos-avro-topic", pedidoPrueba.getId().toString(), pedidoPrueba);

		boolean mensajeRecibido = latch.await(5, TimeUnit.SECONDS);
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

		PedidoAvro pedidoDuplicado = PedidoAvro.newBuilder().setId("TEST-DUPLICADO-99").setProducto("Teclado RGB")
				.setCantidad(7).build();

		kafkaTemplate.send("pedidos-avro-topic", pedidoDuplicado.getId().toString(), pedidoDuplicado);
		kafkaTemplate.send("pedidos-avro-topic", pedidoDuplicado.getId().toString(), pedidoDuplicado);

		boolean ambosRecibidos = latch.await(5, TimeUnit.SECONDS);
		assertTrue(ambosRecibidos, "Ambos mensajes deberían haber sido leídos por el listener");

		verify(consumer, times(2)).procesarPedido(any(PedidoAvro.class), anyInt(), anyLong(),
				any(Acknowledgment.class));

		verify(inventarioPort, times(1)).descontarStock("Teclado RGB", 7);
	}
}