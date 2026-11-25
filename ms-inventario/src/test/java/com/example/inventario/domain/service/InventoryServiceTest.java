package com.example.inventario.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.inventario.domain.exception.InsufficientStockException;
import com.example.inventario.domain.exception.ProductAlreadyExistsException;
import com.example.inventario.domain.exception.ProductNotFoundException;
import com.example.inventario.domain.model.ProductStock;
import com.example.inventario.domain.port.out.ProductStockRepositoryPort;

class InventoryServiceTest {

    private ProductStockRepositoryPort repositoryPort;
    private InventoryService inventoryService;

    @BeforeEach
    void setUp() {
        repositoryPort = mock(ProductStockRepositoryPort.class);
        inventoryService = new InventoryService(repositoryPort);
    }

    @Test
    void shouldDecreaseStockAndSaveWhenEnoughStockExists() {
        // Given
        ProductStock existingProduct = new ProductStock("PROD-100", 10);
        when(repositoryPort.findByProductCode("PROD-100")).thenReturn(Optional.of(existingProduct));

        // When
        inventoryService.decreaseStock("PROD-100", 3);

        // Then
        assertEquals(7, existingProduct.getAvailableQuantity());
        verify(repositoryPort).save(existingProduct);
    }

    @Test
    void shouldThrowExceptionWhenDecreasingMoreThanAvailable() {
        // Given
        ProductStock existingProduct = new ProductStock("PROD-100", 2);
        when(repositoryPort.findByProductCode("PROD-100")).thenReturn(Optional.of(existingProduct));

        // When & Then
        assertThrows(InsufficientStockException.class, () -> inventoryService.decreaseStock("PROD-100", 5));
        verify(repositoryPort, never()).save(any());
    }

    @Test
    void shouldIncreaseStockCorrectly() {
        // Given
        ProductStock existingProduct = new ProductStock("PROD-100", 10);
        when(repositoryPort.findByProductCode("PROD-100")).thenReturn(Optional.of(existingProduct));

        // When
        inventoryService.increaseStock("PROD-100", 15);

        // Then
        assertEquals(25, existingProduct.getAvailableQuantity());
        verify(repositoryPort).save(existingProduct);
    }

    @Test
    void shouldThrowExceptionWhenCreatingDuplicateProduct() {
        // Given
        when(repositoryPort.findByProductCode("PROD-100")).thenReturn(Optional.of(new ProductStock("PROD-100", 10)));

        // When & Then
        assertThrows(ProductAlreadyExistsException.class, () -> inventoryService.createProduct("PROD-100", 5));
        verify(repositoryPort, never()).save(any());
    }

    @Test
    void shouldReturnStockAvailabilityCorrectly() {
        when(repositoryPort.findByProductCode("PROD-100")).thenReturn(Optional.of(new ProductStock("PROD-100", 5)));
        when(repositoryPort.findByProductCode("INEXISTENTE")).thenReturn(Optional.empty());

        assertTrue(inventoryService.hasAvailableStock("PROD-100", 5));
        assertFalse(inventoryService.hasAvailableStock("PROD-100", 6));
        assertFalse(inventoryService.hasAvailableStock("INEXISTENTE", 1));
    }
}
