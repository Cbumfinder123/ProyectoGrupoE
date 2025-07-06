package com.reptilg.services;

import com.reptilg.models.Plataforma;
import com.reptilg.repositories.IPlataformaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PlataformaService {
    private final IPlataformaRepository plataformaRepository;

    public List<Plataforma> listarTodos() {
        return plataformaRepository.findAll();
    }
}