package com.example.pedidos.domain.model;

import java.math.BigDecimal;

public record CreateOrderCommand(String productCode, int quantity, BigDecimal totalAmount, String customerEmail) {
}
