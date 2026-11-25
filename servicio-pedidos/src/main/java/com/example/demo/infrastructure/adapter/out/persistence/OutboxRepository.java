package com.example.demo.infrastructure.adapter.out.persistence;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OutboxRepository extends JpaRepository<OutboxEvent, Long> {
	List<OutboxEvent> findByEnviadoFalse();
}
