package com.reptilg.services;

import com.reptilg.models.*;
import com.reptilg.repositories.IDetalleVentaRepository;
import com.reptilg.repositories.IProductoRepository;
import com.reptilg.repositories.IVentaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class VentaService {
    private final IVentaRepository ventaRepository;
    private final IDetalleVentaRepository detalleVentaRepository;
    private final IProductoRepository productoRepository;
    private final ClienteService clienteService;
    private final ReporteService reporteService; 

    public List<Venta> listarTodas() {
        return ventaRepository.findAllByOrderByFechaDesc();
    }

    public List<Venta> buscar(String query) {
        return ventaRepository.buscar(query);
    }

    public Venta registrarVenta(Venta venta) {
 
        if (venta.getCliente().getId() == null) {
            clienteService.guardar(venta.getCliente());
        }


        venta.setFecha(LocalDateTime.now());


        double total = venta.getDetalles().stream()
                .mapToDouble(d -> d.getCantidad() * d.getPrecioUnitario())
                .sum();
        venta.setTotal(total);

        Venta ventaGuardada = ventaRepository.save(venta);

        for (DetalleVenta detalle : venta.getDetalles()) {
            detalle.setVenta(ventaGuardada);

            Producto producto = productoRepository.findById(detalle.getProducto().getId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            producto.setStock(producto.getStock() - detalle.getCantidad());
            productoRepository.save(producto);

            detalleVentaRepository.save(detalle);
        }

        reporteService.registrarReporte(
            "VENTA", 
            "REGISTRAR", 
            "Venta a " + ventaGuardada.getCliente().getNombre(),
            ventaGuardada.getId(),
            "VEN-" + String.format("%03d", ventaGuardada.getId())
        );

        return ventaGuardada;
    }

    public Venta obtenerPorId(Integer id) {
        return ventaRepository.findById(id).orElseThrow();
    }
}