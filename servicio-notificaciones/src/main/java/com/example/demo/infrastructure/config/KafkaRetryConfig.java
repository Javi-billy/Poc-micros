package com.example.demo.infrastructure.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ContainerProperties.AckMode;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;

import com.example.demo.domain.model.PedidoDTO;

import io.confluent.kafka.serializers.KafkaAvroDeserializer;

@Configuration
public class KafkaRetryConfig {

	@Value("${SCHEMA_REGISTRY_URL:http://localhost:8085}")
	private String schemaRegistryUrl;

	@Value("${spring.kafka.bootstrap-servers}")
	private String bootstrapServers;

	@Value("${spring.kafka.consumer.group-id}")
	private String groupId;

	@Bean
	ProducerFactory<String, Object> producerFactory() {
		Map<String, Object> config = new HashMap<>();
		config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JacksonJsonSerializer.class);
		return new DefaultKafkaProducerFactory<>(config);
	}

	@Bean
	KafkaTemplate<String, Object> defaultRetryTopicKafkaTemplate() {
		return new KafkaTemplate<>(producerFactory());
	}

	@Bean
	ConsumerFactory<String, PedidoDTO> consumerFactory() {
		Map<String, Object> config = new HashMap<>();
		config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		config.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
		config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

		// 1. Desactivamos el commit automático de Kafka
		config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

		/*
		 * JacksonJsonDeserializer<PedidoDTO> jsonDeserializer = new
		 * JacksonJsonDeserializer<>(PedidoDTO.class);
		 * jsonDeserializer.addTrustedPackages("*");
		 * jsonDeserializer.setUseTypeHeaders(false);
		 * 
		 * return new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(),
		 * jsonDeserializer);
		 */

		config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);
		config.put("schema.registry.url", schemaRegistryUrl);
		config.put("specific.avro.reader", true);

		return new DefaultKafkaConsumerFactory<>(config);
	}

	@Bean
	ConcurrentKafkaListenerContainerFactory<String, PedidoDTO> kafkaListenerContainerFactory() {
		ConcurrentKafkaListenerContainerFactory<String, PedidoDTO> factory = new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(consumerFactory());

		// 2. Exigimos confirmación manual inmediata en el código
		factory.getContainerProperties().setAckMode(AckMode.MANUAL_IMMEDIATE);
		return factory;
	}
}