package com.reptilg.repositories;

import com.reptilg.models.DetalleVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.repository.query.Param;

public interface IDetalleVentaRepository extends JpaRepository<DetalleVenta, Integer> {

    @Transactional
    @Modifying
    @Query("DELETE FROM DetalleVenta d WHERE d.producto.id = :productoId")
    void deleteByProductoId(@Param("productoId") Integer productoId);
}
