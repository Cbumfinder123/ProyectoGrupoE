package com.reptilg.services;

import com.reptilg.dtos.AutenticacionFilter;
import com.reptilg.models.Usuario;
import com.reptilg.repositories.IUsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AutenticacionService {

    private final IUsuarioRepository usuarioRepository;

    public Usuario autenticar(AutenticacionFilter filtro) {
        return usuarioRepository.findByUsuarioAndPassword(
            filtro.getUsuario(), 
            filtro.getPassword()
        );
    }
}
