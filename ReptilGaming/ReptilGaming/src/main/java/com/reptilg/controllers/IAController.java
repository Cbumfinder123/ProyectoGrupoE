package com.reptilg.controllers;

import com.reptilg.services.IAService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador que expone un endpoint REST para interactuar con el chatbot ReptilGPT.
 * Envía la pregunta recibida al servicio IAService, que consulta a la IA (DeepSeek vía OpenRouter),
 * y devuelve la respuesta al frontend.
 */

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class IAController {

    private final IAService iaService;

    /**
     * Endpoint POST que recibe una pregunta en texto plano y devuelve la respuesta de la IA.
     * @param pregunta Pregunta enviada por el usuario desde el cliente.
     * @return Respuesta generada por la IA.
     */
    @PostMapping
    public ResponseEntity<String> responder(@RequestBody String pregunta) {
        String respuesta = iaService.consultar(pregunta);
        return ResponseEntity.ok(respuesta);
    }

    /**
     * Opcional: Endpoint GET que podría usarse para comprobar que el bot está activo.
     */
    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("ReptilGPT listo para responder.");
    }
}
