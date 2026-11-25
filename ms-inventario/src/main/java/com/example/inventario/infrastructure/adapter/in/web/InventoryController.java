package com.example.inventario.infrastructure.adapter.in.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.inventario.domain.model.ProductStock;
import com.example.inventario.domain.port.in.CheckStockUseCase;
import com.example.inventario.domain.port.in.CreateProductUseCase;
import com.example.inventario.domain.port.in.DecreaseStockUseCase;
import com.example.inventario.domain.port.in.GetStockQuantityUseCase;
import com.example.inventario.domain.port.in.IncreaseStockUseCase;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/inventory")
@Tag(name = "Inventario", description = "Gestión de productos y control de existencias")
public class InventoryController {

	private final CheckStockUseCase checkStockUseCase;
	private final DecreaseStockUseCase decreaseStockUseCase;
	private final GetStockQuantityUseCase getStockQuantityUseCase;
	private final CreateProductUseCase createProductUseCase;
	private final IncreaseStockUseCase increaseStockUseCase;

	public InventoryController(CheckStockUseCase checkStockUseCase, DecreaseStockUseCase decreaseStockUseCase,
			GetStockQuantityUseCase getStockQuantityUseCase, CreateProductUseCase createProductUseCase,
			IncreaseStockUseCase increaseStockUseCase) {
		this.checkStockUseCase = checkStockUseCase;
		this.decreaseStockUseCase = decreaseStockUseCase;
		this.getStockQuantityUseCase = getStockQuantityUseCase;
		this.createProductUseCase = createProductUseCase;
		this.increaseStockUseCase = increaseStockUseCase;
	}

	/**
	 * @param productCode
	 * @param quantity
	 * @return StockResponse
	 */
	@Operation(summary = "Comprobar disponibilidad de stock", description = "Verifica si hay unidades suficientes para un pedido. Consumido internamente vía Feign por ms-pedidos.")
	@GetMapping("/{productCode}")
	public StockResponse checkStock(@PathVariable String productCode, @RequestParam int quantity) {
		boolean available = checkStockUseCase.hasAvailableStock(productCode, quantity);
		return new StockResponse(productCode, available);
	}

	/**
	 * @param productCode
	 * @param quantity
	 */
	@Operation(summary = "Descontar stock", description = "Resta unidades del inventario disponible. Consumido vía Feign por el consumidor de Kafka al procesar una venta.")
	@PostMapping("/{productCode}/decrease")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void decreaseStock(@PathVariable String productCode, @RequestParam int quantity) {
		decreaseStockUseCase.decreaseStock(productCode, quantity);
	}

	/**
	 * @param productCode
	 * @return QuantityResponse
	 */
	@Operation(summary = "Consultar cantidad exacta", description = "Devuelve el número total de unidades disponibles actualmente para un producto.")
	@GetMapping("/{productCode}/quantity")
	public QuantityResponse getQuantity(@PathVariable String productCode) {
		int cantidad = getStockQuantityUseCase.getAvailableQuantity(productCode);
		return new QuantityResponse(productCode, cantidad);
	}

	/**
	 * @param productCode
	 * @param quantity
	 */
	@Operation(summary = "Aumentar stock", description = "Suma unidades al inventario existente de un producto (reposiciones o devoluciones).")
	@PostMapping("/{productCode}/increase")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void increaseStock(@PathVariable String productCode, @RequestParam int quantity) {
		increaseStockUseCase.increaseStock(productCode, quantity);
	}

	/**
	 * @param request
	 * @return QuantityResponse
	 */
	@Operation(summary = "Crear nuevo producto", description = "Registra un nuevo código de producto en el sistema con su stock inicial.")
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)	
	public QuantityResponse createProduct(@RequestBody CreateProductRequest request) {
		ProductStock product = createProductUseCase.createProduct(request.productCode(), request.quantity());
		return new QuantityResponse(product.getProductCode(), product.getAvailableQuantity());
	}

	public record CreateProductRequest(String productCode, int quantity) {
	}

	public record StockResponse(String productCode, boolean available) {
	}

	public record QuantityResponse(String productCode, int quantity) {
	}
}