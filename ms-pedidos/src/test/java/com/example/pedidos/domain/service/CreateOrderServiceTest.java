package com.example.pedidos.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.pedidos.domain.exception.InsufficientStockException;
import com.example.pedidos.domain.model.CreateOrderCommand;
import com.example.pedidos.domain.model.Order;
import com.example.pedidos.domain.port.out.EventPublisherPort;
import com.example.pedidos.domain.port.out.InventoryPort;
import com.example.pedidos.domain.port.out.OrderRepositoryPort;

class CreateOrderServiceTest {

	private InventoryPort inventoryPort;
	private OrderRepositoryPort orderRepositoryPort;
	private EventPublisherPort eventPublisherPort;
	private CreateOrderService createOrderService;

	@BeforeEach
	void setUp() {
		// Creamos mocks puros de los puertos de salida (sin levantar Spring, Feign ni
		// JPA)
		inventoryPort = mock(InventoryPort.class);
		orderRepositoryPort = mock(OrderRepositoryPort.class);
		eventPublisherPort = mock(EventPublisherPort.class);

		createOrderService = new CreateOrderService(inventoryPort, orderRepositoryPort, eventPublisherPort);
	}

	@Test
	void shouldCreateConfirmAndPublishOrderWhenStockIsAvailable() {
		// Given (Preparación)
		CreateOrderCommand command = new CreateOrderCommand("PROD-100", 2, new BigDecimal("99.98"), "cliente@test.com");
		when(inventoryPort.hasStock("PROD-100", 2)).thenReturn(true);
		when(orderRepositoryPort.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

		// When (Ejecución)
		Order result = createOrderService.createOrder(command);

		// Then (Verificación)
		assertNotNull(result.getId());
		assertEquals("CONFIRMED", result.getStatus());
		assertEquals("PROD-100", result.getProductCode());
		assertEquals(2, result.getQuantity());

		verify(inventoryPort, times(1)).hasStock("PROD-100", 2);
		verify(orderRepositoryPort, times(1)).save(any(Order.class));
		verify(eventPublisherPort, times(1)).publishOrderCreated(result);
	}

	@Test
	void shouldThrowExceptionAndNotSaveOrPublishWhenStockIsInsufficient() {
		// Given
		CreateOrderCommand command = new CreateOrderCommand("PROD-100", 50, new BigDecimal("500.00"),
				"cliente@test.com");
		when(inventoryPort.hasStock("PROD-100", 50)).thenReturn(false);

		// When & Then
		assertThrows(InsufficientStockException.class, () -> createOrderService.createOrder(command));

		// Verificamos que si no hay stock, NUNCA guarda en BD ni envía evento a Kafka
		verify(orderRepositoryPort, never()).save(any());
		verify(eventPublisherPort, never()).publishOrderCreated(any());
	}
}
