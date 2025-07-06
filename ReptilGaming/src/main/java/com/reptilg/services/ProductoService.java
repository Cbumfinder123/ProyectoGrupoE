package com.reptilg.services;

import com.reptilg.dtos.ResultadoResponse;
import com.reptilg.models.Producto;
import com.reptilg.repositories.IProductoRepository;
import com.reptilg.repositories.IDetalleVentaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductoService {
    private final IProductoRepository productoRepository;
    private final IDetalleVentaRepository detalleVentaRepository;
    private final ReporteService reporteService;

    public List<Producto> listarTodos() {
        return productoRepository.findAllByOrderByNombreAsc();
    }

    public List<Producto> buscar(String query) {
        return productoRepository.buscar(query.toLowerCase());
    }

    public Producto guardar(Producto producto, String usuarioResponsable) {
        boolean esNuevo = (producto.getId() == null);

        if (esNuevo) {
            String ultimoCodigo = productoRepository.obtenerUltimoCodigo();
            String nuevoCodigo = generarSiguienteCodigo(ultimoCodigo);
            producto.setCodigo(nuevoCodigo);
        }

        Producto productoGuardado = productoRepository.save(producto);

        reporteService.registrarReporte(
            "PRODUCTO",
            esNuevo ? "CREAR" : "ACTUALIZAR",
            "Producto: " + producto.getNombre(),
            productoGuardado.getId(),
            productoGuardado.getCodigo(),
            usuarioResponsable
        );

        return productoGuardado;
    }

    public ResultadoResponse eliminar(Integer id, String usuarioResponsable) {
        try {
            Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            detalleVentaRepository.deleteByProductoId(id);
            productoRepository.deleteById(id);

            reporteService.registrarReporte(
                "PRODUCTO",
                "ELIMINAR",
                "Producto: " + producto.getNombre(),
                producto.getId(),
                producto.getCodigo(),
                usuarioResponsable
            );

            return new ResultadoResponse(true, "Producto eliminado correctamente");
        } catch (Exception ex) {
            return new ResultadoResponse(false, "Error al eliminar: " + ex.getMessage());
        }
    }

    public Producto obtenerPorId(Integer id) {
        return productoRepository.findById(id).orElseThrow();
    }

    private String generarSiguienteCodigo(String ultimoCodigo) {
        if (ultimoCodigo == null || !ultimoCodigo.startsWith("PROD-")) {
            return "PROD-001";
        }

        try {
            int numero = Integer.parseInt(ultimoCodigo.substring(5));
            return String.format("PROD-%03d", numero + 1);
        } catch (NumberFormatException e) {
            return "PROD-001";
        }
    }
}
