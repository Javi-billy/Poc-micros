package com.example.ia.tools;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import com.example.ia.client.InventarioClient;
import com.example.ia.client.PedidosClient;
import com.example.ia.client.PedidosClient.CreateOrderRequest;
import com.example.ia.client.PedidosClient.OrderResponse;

@Component
public class TiendaTools {

	private final InventarioClient inventarioClient;
	private final PedidosClient pedidosClient;

	public TiendaTools(InventarioClient inventarioClient, PedidosClient pedidosClient) {
		this.inventarioClient = inventarioClient;
		this.pedidosClient = pedidosClient;
	}

	@Tool(description = "Consulta el stock disponible (cantidad de unidades) de un producto dado su código, por ejemplo PROD-100")
	public String consultarStock(
			@ToolParam(description = "Código del producto a consultar, por ejemplo PROD-100") String productCode) {
		try {
			var cantidad = inventarioClient.consultarCantidad(productCode);
			return "El producto " + productCode + " tiene " + cantidad + " unidades disponibles en inventario.";
		} catch (Exception e) {
			e.printStackTrace();
			return "No se encontró el producto " + productCode + " o hubo un error al consultar el inventario.";
		}
	}

	@Tool(description = "Crea un nuevo pedido de compra para un cliente")
	public String crearPedido(@ToolParam(description = "Código del producto, por ejemplo PROD-100") String productCode,
			@ToolParam(description = "Cantidad de unidades a comprar") int quantity,
			@ToolParam(description = "Importe total del pedido en euros") double totalAmount,
			@ToolParam(description = "Correo electrónico del cliente") String customerEmail) {
		try {
			OrderResponse response = pedidosClient.crearPedido(
					new CreateOrderRequest(productCode, quantity, BigDecimal.valueOf(totalAmount), customerEmail));
			return "Pedido creado correctamente. ID del pedido: " + response.orderId() + " | Estado inicial: "
					+ response.status();
		} catch (Exception e) {
			e.printStackTrace();
			return "No se pudo crear el pedido: " + e.getMessage();
		}
	}

	@Tool(description = "Consulta el estado actual de un pedido (PENDING, CONFIRMED o CANCELLED) mediante su UUID")
	public String consultarPedido(@ToolParam(description = "Identificador UUID del pedido") String orderId) {
		try {
			OrderResponse response = pedidosClient.consultarPedido(UUID.fromString(orderId));
			return "El pedido " + response.orderId() + " (" + response.quantity() + " uds de " + response.productCode()
					+ ") está en estado: " + response.status();
		} catch (Exception e) {
			e.printStackTrace();
			return "No se encontró ningún pedido con el ID: " + orderId;
		}
	}

	@Tool(description = "Lista todos los pedidos registrados en el sistema")
	public List<OrderResponse> listarPedidos() {
		return pedidosClient.listarPedidos();
	}

	@Tool(description = "Cancela un pedido confirmado mediante su UUID y repone su stock")
	public String cancelarPedido(@ToolParam(description = "Identificador UUID del pedido a cancelar") String orderId) {
		try {
			OrderResponse response = pedidosClient.cancelarPedido(UUID.fromString(orderId));
			return "Pedido " + response.orderId() + " cancelado con éxito. Nuevo estado: " + response.status();
		} catch (Exception e) {
			e.printStackTrace();
			return "Error al cancelar el pedido " + orderId + ": " + e.getMessage();
		}
	}
}
