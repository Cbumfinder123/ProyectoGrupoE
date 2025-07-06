package com.reptilg.controllers;

import com.reptilg.models.Reporte;
import com.reptilg.services.ReporteService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/reportes")
public class ReporteController {

    private final ReporteService reporteService;

    public ReporteController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    @GetMapping("/listado")
    public String listado(Model model,
                          @RequestParam(required = false) String tipo,
                          @RequestParam(required = false) String fecha,
                          HttpSession session) {
        if (session.getAttribute("cuenta") == null)
            return "redirect:/login";

        List<Reporte> reportes;

        if ((tipo != null && !tipo.isEmpty()) && (fecha != null && !fecha.isEmpty())) {
            LocalDate fechaFiltro = LocalDate.parse(fecha, DateTimeFormatter.ISO_DATE);
            reportes = reporteService.buscarPorTipoYFecha(tipo, fechaFiltro);
        } else if (fecha != null && !fecha.isEmpty()) {
            LocalDate fechaFiltro = LocalDate.parse(fecha, DateTimeFormatter.ISO_DATE);
            reportes = reporteService.buscarPorFecha(fechaFiltro);
        } else if (tipo != null && !tipo.isEmpty()) {
            reportes = reporteService.buscarPorTipo(tipo);
        } else {
            reportes = reporteService.listarTodos();
        }

        model.addAttribute("reportes", reportes);
        model.addAttribute("tipo", tipo);
        model.addAttribute("fecha", fecha);
        return "reportes/listado";
    }
}
