package com.example.demo.infrastructure.adapter.out.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class OutboxEvent {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String pedidoId;

	private String producto;

	private int cantidad;

	private boolean enviado = false;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPedidoId() {
		return pedidoId;
	}

	public void setPedidoId(String pedidoId) {
		this.pedidoId = pedidoId;
	}

	public String getProducto() {
		return producto;
	}

	public void setProducto(String producto) {
		this.producto = producto;
	}

	public int getCantidad() {
		return cantidad;
	}

	public void setCantidad(int cantidad) {
		this.cantidad = cantidad;
	}

	public boolean isEnviado() {
		return enviado;
	}

	public void setEnviado(boolean enviado) {
		this.enviado = enviado;
	}

}
