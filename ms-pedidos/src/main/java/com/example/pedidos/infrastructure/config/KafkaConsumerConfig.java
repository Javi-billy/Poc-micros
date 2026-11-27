package com.example.pedidos.infrastructure.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;

import com.example.pedidos.infrastructure.adapter.in.messaging.OrderSagaResponseListener.OrderSagaResponseEvent;

@Configuration
public class KafkaConsumerConfig {

	@Value("${spring.kafka.bootstrap-servers:localhost:9092}")
	private String bootstrapServers;

	@Bean
	ConsumerFactory<String, OrderSagaResponseEvent> consumerFactory() {
		Map<String, Object> props = new HashMap<>();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		props.put(ConsumerConfig.GROUP_ID_CONFIG, "pedidos-saga-group");
		props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

		JacksonJsonDeserializer<OrderSagaResponseEvent> deserializer = new JacksonJsonDeserializer<>(
				OrderSagaResponseEvent.class);
		deserializer.addTrustedPackages("*");
		deserializer.setUseTypeHeaders(false);

		return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
	}

	@Bean
	ConcurrentKafkaListenerContainerFactory<String, OrderSagaResponseEvent> kafkaListenerContainerFactory() {
		ConcurrentKafkaListenerContainerFactory<String, OrderSagaResponseEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(consumerFactory());
		return factory;
	}
}
