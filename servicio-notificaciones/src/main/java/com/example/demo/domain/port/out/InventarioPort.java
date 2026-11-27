package com.example.demo.domain.port.out;

public interface InventarioPort {

	void descontarStock(String codigoProducto, int cantidad);
	
	void incrementarStock(String codigoProducto, int cantidad);
}
