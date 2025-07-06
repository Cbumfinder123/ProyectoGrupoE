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
import java.util.Optional;
import java.util.stream.Collectors;

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

    public Venta obtenerPorId(Integer id) {
        return ventaRepository.findById(id).orElseThrow();
    }

    // 🔧 Nuevo método con usuario
    public Venta registrarVenta(Venta venta, String usuarioResponsable) {
        Cliente cliente = manejarCliente(venta.getCliente());
        venta.setCliente(cliente);

        venta.setFecha(LocalDateTime.now());
        calcularTotalVenta(venta);

        Venta ventaGuardada = ventaRepository.save(venta);
        procesarDetalles(ventaGuardada);

        registrarReporteVenta(ventaGuardada, usuarioResponsable); // ahora con usuario

        return ventaGuardada;
    }

    private Cliente manejarCliente(Cliente cliente) {
        if (cliente.getId() == null) {
            Optional<Cliente> existente = clienteService.buscarPorDni(cliente.getDni());
            if (existente.isPresent()) {
                return existente.get();
            }
        }
        return clienteService.guardar(cliente);
    }

    private void calcularTotalVenta(Venta venta) {
        double total = venta.getDetalles().stream()
                .mapToDouble(d -> d.getCantidad() * d.getPrecioUnitario())
                .sum();
        venta.setTotal(total);
    }

    private void procesarDetalles(Venta venta) {
        for (DetalleVenta detalle : venta.getDetalles()) {
            detalle.setVenta(venta);

            Producto producto = productoRepository.findById(detalle.getProducto().getId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            validarStock(producto, detalle.getCantidad());
            actualizarStock(producto, detalle.getCantidad());

            detalleVentaRepository.save(detalle);
        }
    }

    public List<Venta> filtrarPorFecha(List<Venta> ventas, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return ventas.stream()
                .filter(v -> v.getFecha().isAfter(fechaInicio) && v.getFecha().isBefore(fechaFin))
                .collect(Collectors.toList());
    }

    private void validarStock(Producto producto, int cantidad) {
        if (producto.getStock() < cantidad) {
            throw new RuntimeException("Stock insuficiente para " + producto.getNombre() +
                    ". Stock actual: " + producto.getStock());
        }
    }

    private void actualizarStock(Producto producto, int cantidad) {
        producto.setStock(producto.getStock() - cantidad);
        productoRepository.save(producto);
    }

    // ✍️ Modificado para recibir el nombre del usuario
    private void registrarReporteVenta(Venta venta, String usuario) {
        reporteService.registrarReporte(
                "VENTA",
                "REGISTRAR",
                "Venta a " + venta.getCliente().getNombre(),
                venta.getId(),
                "VEN-" + String.format("%03d", venta.getId()),
                usuario
        );
    }
}
