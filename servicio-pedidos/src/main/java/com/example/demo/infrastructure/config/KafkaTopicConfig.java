package com.example.demo.infrastructure.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

	@Bean
	NewTopic pedidosEscalablesTopic() {
		// 3 carriles paralelos
		return TopicBuilder.name("pedidos-escalables-topic").partitions(3).replicas(1).build();
	}
}
