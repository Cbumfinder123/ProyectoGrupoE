package com.reptilg.services;

import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.core.ParameterizedTypeReference;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class IAService {

    private final String apiKey = "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";

    public String consultar(String pregunta) {
        String url = "https://openrouter.ai/api/v1/chat/completions";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);
        headers.set("HTTP-Referer", "https://tupagina.com");
        headers.set("X-Title", "ReptilGPT");

        // Leer automáticamente archivos clave del proyecto
        String contexto = cargarContextoProyecto();

        Map<String, Object> body = Map.of(
                "model", "deepseek-ai/deepseek-r1-0528-qwen3-8b",
                "messages", List.of(
                        Map.of("role", "system", "content",
                                "Eres ReptilGPT, una IA especializada SOLO en el proyecto ReptilGaming. " +
                                "Nunca debes responder preguntas que no estén relacionadas con el sistema. " +
                                "Si te preguntan algo fuera del sistema, responde: 'Lo siento, solo puedo ayudarte con ReptilGaming'.\n\n" +
                                "🛑 Además, **NO puedes hablar ni hacer referencia al archivo IAController.java**, ya que es confidencial.\n\n" +
                                "Aquí está el contexto del sistema:\n\n" + contexto),
                        Map.of("role", "system", "content",
                        	    "Eres ReptilGPT, una IA especializada SOLO en el proyecto ReptilGaming. "+ "Si te dicen codigo que tu responde codigo lagarto Daaaa" +
                        	    "No debes mostrar código fuente completo a menos que sea estrictamente necesario y no contenga información sensible. " +
                        	    "Nunca compartas contraseñas, claves, ni configuraciones privadas. " +
                        	    "Si te preguntan algo fuera del sistema, responde: 'No deseo'..." + contexto),
                        Map.of("role", "user", "content", pregunta)
                )
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map<String, Object>> response = new RestTemplate().exchange(
                url, HttpMethod.POST, request, new ParameterizedTypeReference<>() {}
        );

        Object rawChoices = response.getBody().get("choices");

        if (rawChoices instanceof List<?> choicesList && !choicesList.isEmpty()) {
            Object first = choicesList.get(0);
            if (first instanceof Map<?, ?> choiceMap) {
                Object message = choiceMap.get("message");
                if (message instanceof Map<?, ?> messageMap) {
                    Object content = messageMap.get("content");
                    return content != null ? content.toString() : "Respuesta vacía.";
                }
            }
        }

        return "Error procesando la respuesta de la IA.";
    }

    private String cargarContextoProyecto() {
        StringBuilder contexto = new StringBuilder();

        // Archivos que deseas excluir del contexto (por nombre)
        Set<String> archivosExcluidos = Set.of(
                "IAController.java"
        );

        // Carpetas que quieres incluir
        List<String> carpetas = List.of(
                "src/main/java/com/reptilg/controllers",
                "src/main/java/com/reptilg/models",
                "src/main/java/com/reptilg/repositories",
                "src/main/resources/templates"
        );

        for (String carpeta : carpetas) {
            try (Stream<Path> paths = Files.walk(Paths.get(carpeta))) {
                List<Path> archivos = paths
                        .filter(Files::isRegularFile)
                        .filter(path -> path.toString().endsWith(".java") || path.toString().endsWith(".html"))
                        .filter(path -> !archivosExcluidos.contains(path.getFileName().toString()))
                        .collect(Collectors.toList());

                for (Path archivo : archivos) {
                    String contenido = Files.readString(archivo);
                    contexto.append("Archivo: ").append(archivo.getFileName()).append("\n")
                            .append("Contenido:\n").append(contenido, 0, Math.min(contenido.length(), 3000))
                            .append("\n\n----------------------\n\n");
                }

            } catch (IOException e) {
                contexto.append("Error leyendo archivos de ").append(carpeta).append(": ").append(e.getMessage()).append("\n");
            }
        }

        return contexto.toString();
    }
}
