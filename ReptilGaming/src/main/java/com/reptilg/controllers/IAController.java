package com.reptilg.controllers;

import com.reptilg.services.IAService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class IAController {

    private final IAService iaService;


    @PostMapping
    public ResponseEntity<String> responder(@RequestBody String pregunta) {
        String respuesta = iaService.consultar(pregunta);
        return ResponseEntity.ok(respuesta);
    }


    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("ReptilGPT listo para responder.");
    }
}
