package com.example.inventario.infrastructure.adapter.out.jpa;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.example.inventario.domain.model.ProductStock;
import com.example.inventario.domain.port.out.ProductStockRepositoryPort;

@Component
public class InventoryPersistenceAdapter implements ProductStockRepositoryPort {

	private final SpringDataProductStockRepository repository;

	public InventoryPersistenceAdapter(SpringDataProductStockRepository repository) {
		this.repository = repository;
	}

	@Override
	public Optional<ProductStock> findByProductCode(String productCode) {
		return repository.findById(productCode)
				.map(entity -> new ProductStock(entity.getProductCode(), entity.getAvailableQuantity()));
	}

	@Override
	public ProductStock save(ProductStock productStock) {
		// Si ya existe en BD, recuperamos la entidad con su @Version actual
		ProductStockJpaEntity entity = repository.findById(productStock.getProductCode()).orElseGet(
				() -> new ProductStockJpaEntity(productStock.getProductCode(), productStock.getAvailableQuantity()));

		entity.setAvailableQuantity(productStock.getAvailableQuantity());

		ProductStockJpaEntity saved = repository.save(entity);
		return new ProductStock(saved.getProductCode(), saved.getAvailableQuantity());
	}
}