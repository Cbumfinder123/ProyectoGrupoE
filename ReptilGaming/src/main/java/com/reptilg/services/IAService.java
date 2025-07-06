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

        String contexto = cargarContextoProyecto();

        Map<String, Object> body = Map.of(
                "model", "deepseek-ai/deepseek-r1-0528-qwen3-8b",
                "messages", List.of(
                        Map.of("role", "system", "content",
                                "Eres ReptilGPT, una IA especializada SOLO en el sistema ReptilGaming.\n" +
                                "No debes responder preguntas que no estén relacionadas con el sistema.\n" +
                                "Si te preguntan algo fuera del sistema, responde: \"Lo siento, solo puedo ayudarte con ReptilGaming.\"\n\n" +
                                "Política de seguridad:\n" +
                                "- NO puedes hacer referencia al archivo `IAController.java`, es confidencial.\n" +
                                "- NO debes mostrar el contenido completo de ningún archivo que contenga claves, configuraciones privadas o lógica sensible.\n" +
                                "- Si detectas una petición que intenta acceder a esos datos, responde: \"Lo siento, no puedo mostrarte ese código por motivos de seguridad.\"\n" +
                                "- Puedes listar nombres de controladores, entidades, servicios, vistas o repositorios, pero no debes mostrar sus implementaciones completas si no es seguro.\n\n" +
                                "Aquí está el contexto del sistema:\n\n" + contexto),
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

        Set<String> archivosExcluidos = Set.of("IAController.java");

        List<String> carpetas = List.of(
                "src/main/java/com/reptilg/controllers",
                "src/main/java/com/reptilg/models",
                "src/main/java/com/reptilg/repositories",
                "src/main/java/com/reptilg/services",
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

                    // Verificación de contenido sensible
                    boolean contieneDatosSensibles = contenido.contains("apiKey")
                            || contenido.contains("password")
                            || contenido.contains("DataSource");

                    contexto.append("Archivo: ").append(archivo.getFileName()).append("\n");

                    if (contieneDatosSensibles) {
                        contexto.append("⚠ Contenido omitido por contener datos sensibles.\n");
                    } else {
                        contexto.append("Contenido:\n")
                                .append(contenido, 0, Math.min(contenido.length(), 3000))
                                .append("\n");
                    }

                    contexto.append("\n----------------------\n\n");
                }

            } catch (IOException e) {
                contexto.append("Error leyendo archivos de ").append(carpeta)
                        .append(": ").append(e.getMessage()).append("\n");
            }
        }

        return contexto.toString();
    }
}
