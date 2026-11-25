package com.example.inventario.infrastructure.adapter.out.jpa;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

@Entity
@Table(name = "product_stock")
public class ProductStockJpaEntity {

	@Id
	private String productCode;

	private int availableQuantity;

	@Version
	private Long version;

	public ProductStockJpaEntity() {
	}

	public ProductStockJpaEntity(String productCode, int availableQuantity) {
		this.productCode = productCode;
		this.availableQuantity = availableQuantity;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public int getAvailableQuantity() {
		return availableQuantity;
	}

	public void setAvailableQuantity(int availableQuantity) {
		this.availableQuantity = availableQuantity;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

}