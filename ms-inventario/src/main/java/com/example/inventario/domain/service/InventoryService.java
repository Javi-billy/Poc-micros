package com.example.inventario.domain.service;

import com.example.inventario.domain.exception.ProductAlreadyExistsException;
import com.example.inventario.domain.exception.ProductNotFoundException;
import com.example.inventario.domain.model.ProductStock;
import com.example.inventario.domain.port.in.CheckStockUseCase;
import com.example.inventario.domain.port.in.CreateProductUseCase;
import com.example.inventario.domain.port.in.DecreaseStockUseCase;
import com.example.inventario.domain.port.in.GetStockQuantityUseCase;
import com.example.inventario.domain.port.in.IncreaseStockUseCase;
import com.example.inventario.domain.port.out.ProductStockRepositoryPort;

public class InventoryService implements CheckStockUseCase, DecreaseStockUseCase, GetStockQuantityUseCase,
		CreateProductUseCase, IncreaseStockUseCase {

	private final ProductStockRepositoryPort repositoryPort;

	public InventoryService(ProductStockRepositoryPort repositoryPort) {
		this.repositoryPort = repositoryPort;
	}

	@Override
	public boolean hasAvailableStock(String productCode, int quantity) {
		return repositoryPort.findByProductCode(productCode).map(stock -> stock.hasEnoughStock(quantity)).orElse(false);
	}

	@Override
	public void decreaseStock(String productCode, int quantity) {
		ProductStock stock = repositoryPort.findByProductCode(productCode)
				.orElseThrow(() -> new ProductNotFoundException(productCode));

		stock.decreaseStock(quantity);
		repositoryPort.save(stock);
	}

	@Override
	public int getAvailableQuantity(String productCode) {
		return repositoryPort.findByProductCode(productCode).map(ProductStock::getAvailableQuantity)
				.orElseThrow(() -> new ProductNotFoundException(productCode));
	}

	@Override
	public void increaseStock(String productCode, int quantity) {
		ProductStock stock = repositoryPort.findByProductCode(productCode)
				.orElseThrow(() -> new ProductNotFoundException(productCode));
		stock.increaseStock(quantity);
		repositoryPort.save(stock);
	}

	@Override
	public ProductStock createProduct(String productCode, int initialQuantity) {
		repositoryPort.findByProductCode(productCode).ifPresent(stock -> {
			throw new ProductAlreadyExistsException(productCode);
		});

		ProductStock newStock = new ProductStock(productCode, initialQuantity);
		repositoryPort.save(newStock);
		return newStock;
	}

}