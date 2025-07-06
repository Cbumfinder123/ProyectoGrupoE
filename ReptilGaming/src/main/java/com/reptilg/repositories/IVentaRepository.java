package com.reptilg.repositories;

import com.reptilg.models.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface IVentaRepository extends JpaRepository<Venta, Integer> {
    @Query("SELECT v FROM Venta v WHERE " +
           "LOWER(v.cliente.nombre) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(v.cliente.dni) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Venta> buscar(@Param("query") String query);

	List<Venta> findAllByOrderByFechaDesc();
	List<Venta> findByFechaBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);
}