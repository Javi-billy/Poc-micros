package com.example.pedidos.infrastructure.adapter.out.jpa;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

interface SpringDataOrderRepository extends JpaRepository<OrderJpaEntity, UUID> {
}
