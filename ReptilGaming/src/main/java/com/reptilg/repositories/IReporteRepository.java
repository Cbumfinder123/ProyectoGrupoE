package com.reptilg.repositories;

import com.reptilg.models.Reporte;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface IReporteRepository extends JpaRepository<Reporte, Integer> {
    List<Reporte> findByTipoOrderByFechaDesc(String tipo);
    List<Reporte> findByFechaBetweenOrderByFechaDesc(LocalDateTime start, LocalDateTime end);
    List<Reporte> findAllByOrderByFechaDesc();
    List<Reporte> findByTipoAndFechaBetweenOrderByFechaDesc(String tipo, LocalDateTime start, LocalDateTime end);
}
