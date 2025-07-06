package com.reptilg.repositories;

import com.reptilg.models.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IProveedorRepository extends JpaRepository<Proveedor, Integer> {
}