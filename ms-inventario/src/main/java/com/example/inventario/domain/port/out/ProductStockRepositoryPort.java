package com.example.inventario.domain.port.out;

import java.util.Optional;

import com.example.inventario.domain.model.ProductStock;

public interface ProductStockRepositoryPort {

	Optional<ProductStock> findByProductCode(String productCode);

	ProductStock save(ProductStock productStock);

}