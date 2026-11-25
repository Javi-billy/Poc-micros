package com.example.inventario.infrastructure.adapter.out.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

interface SpringDataProductStockRepository extends JpaRepository<ProductStockJpaEntity, String> {}