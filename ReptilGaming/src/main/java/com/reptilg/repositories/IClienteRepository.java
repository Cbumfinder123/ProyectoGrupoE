package com.reptilg.repositories;

import com.reptilg.models.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface IClienteRepository extends JpaRepository<Cliente, Integer> {
    Optional<Cliente> findByDni(String dni);
}