package com.reptilg.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResultadoResponse {
    private boolean success;
    private String mensaje;
    
    public boolean isSuccess() {
        return success;
    }
    
    public String getMensaje() {
        return mensaje;
    }
}