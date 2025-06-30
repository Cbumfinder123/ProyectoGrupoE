package com.reptilg.services;

import com.reptilg.models.Reporte;
import com.reptilg.repositories.IReporteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.time.LocalDate;


@Service
@RequiredArgsConstructor
public class ReporteService {
    private final IReporteRepository reporteRepository;

    public void registrarReporte(String tipo, String accion, String detalle, 
                                Integer entidadId, String codigoEntidad) {
        Reporte reporte = new Reporte();
        reporte.setTipo(tipo);
        reporte.setAccion(accion);
        reporte.setDetalle(detalle);
        reporte.setFecha(LocalDateTime.now());
        reporte.setEntidadId(entidadId);
        reporte.setCodigoEntidad(codigoEntidad);
        reporteRepository.save(reporte);
    }

    public List<Reporte> buscarPorTipo(String tipo) {
        return reporteRepository.findByTipoOrderByFechaDesc(tipo);
    }
    
    public List<Reporte> buscarPorFecha(LocalDate fecha) {
        LocalDateTime startOfDay = fecha.atStartOfDay();
        LocalDateTime endOfDay = fecha.atTime(23, 59, 59);
        return reporteRepository.findByFechaBetweenOrderByFechaDesc(startOfDay, endOfDay);
    }
    
    public List<Reporte> listarTodos() {
        return reporteRepository.findAllByOrderByFechaDesc();
    }
    public List<Reporte> buscarPorTipoYFecha(String tipo, LocalDate fecha) {
        LocalDateTime inicio = fecha.atStartOfDay();
        LocalDateTime fin = fecha.plusDays(1).atStartOfDay().minusNanos(1);
        return reporteRepository.findByTipoAndFechaBetweenOrderByFechaDesc(tipo, inicio, fin);
    }

    
}