package com.reptilg.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class VistaBotController {

    @GetMapping("/bot")
    public String verBot() {
        return "bot/chat"; 
    }
}
