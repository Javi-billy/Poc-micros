package com.example.ia.web;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ia.tools.TiendaTools;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/ai")
@Tag(name = "Asistente IA", description = "Agente conversacional conectado al ecosistema de microservicios")
public class AsistenteController {

	private final ChatClient chatClient;

	public AsistenteController(ChatClient.Builder chatClientBuilder, TiendaTools tiendaTools) {
		this.chatClient = chatClientBuilder
				.defaultSystem("""
						Eres el asistente oficial de la tienda y SÍ tienes acceso en tiempo real al inventario y a los pedidos mediante tus herramientas (tools).
						NUNCA digas que no tienes acceso a información en tiempo real.
						Ante cualquier pregunta sobre productos, stock, cantidades o pedidos, DEBES invocar obligatoriamente la herramienta correspondiente antes de responder.
						Responde siempre en español de forma breve y directa con los datos obtenidos de la herramienta.
						""")
				.defaultTools(tiendaTools)
				.build();
	}

	@Operation(summary = "Hablar con el asistente", description = "Envía una instrucción en lenguaje natural para consultar stock, comprar o cancelar pedidos.")
	@PostMapping("/chat")
	public ChatResponse chat(@RequestBody ChatRequest request) {
		String respuesta = chatClient.prompt()
				.user(request.mensaje())
				.call()
				.content();

		return new ChatResponse(respuesta);
	}

	public record ChatRequest(
			@Schema(example = "¿Cuántas unidades quedan del producto PROD-100?") String mensaje) {
	}

	public record ChatResponse(String respuesta) {
	}
}
