package com.reptilg.services;

import com.reptilg.models.Proveedor;
import com.reptilg.repositories.IProveedorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProveedorService {
    private final IProveedorRepository proveedorRepository;

    public List<Proveedor> listarTodos() {
        return proveedorRepository.findAll();
    }
}