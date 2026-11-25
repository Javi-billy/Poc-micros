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

import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/inventory")
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

	// Consumido vía Feign por ms-pedidos
	@GetMapping("/{productCode}")
	public StockResponse checkStock(@PathVariable String productCode, @RequestParam int quantity) {
		boolean available = checkStockUseCase.hasAvailableStock(productCode, quantity);
		return new StockResponse(productCode, available);
	}

	// Consumido vía Feign por ms-kafka-consumer al procesar el evento
	@PostMapping("/{productCode}/decrease")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void decreaseStock(@PathVariable String productCode, @RequestParam int quantity) {
		decreaseStockUseCase.decreaseStock(productCode, quantity);
	}

	@GetMapping("/{productCode}/quantity")
	public QuantityResponse getQuantity(@PathVariable String productCode) {
		int cantidad = getStockQuantityUseCase.getAvailableQuantity(productCode);
		return new QuantityResponse(productCode, cantidad);
	}

	@PostMapping("/{productCode}/increase")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void increaseStock(@PathVariable String productCode, @RequestParam int quantity) {
		increaseStockUseCase.increaseStock(productCode, quantity);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public QuantityResponse increaseStock(@RequestBody CreateProductRequest request) {
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