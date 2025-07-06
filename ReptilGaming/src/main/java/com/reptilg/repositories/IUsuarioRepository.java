package com.reptilg.repositories;

import com.reptilg.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IUsuarioRepository extends JpaRepository<Usuario, Integer> {
    Usuario findByUsuarioAndPassword(String usuario, String password);
}
