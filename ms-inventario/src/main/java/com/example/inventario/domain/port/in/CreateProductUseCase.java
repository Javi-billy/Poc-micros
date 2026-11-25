package com.example.inventario.domain.port.in;

import com.example.inventario.domain.model.ProductStock;

public interface CreateProductUseCase {

	ProductStock createProduct(String productCode, int initialQuantity);

}