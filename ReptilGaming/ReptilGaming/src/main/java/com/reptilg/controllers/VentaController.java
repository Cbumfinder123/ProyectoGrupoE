package com.reptilg.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reptilg.models.*;
import com.reptilg.services.ProductoService;
import com.reptilg.services.VentaService;
import com.reptilg.utils.Alert;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/ventas")
public class VentaController {

    private final VentaService ventaService;
    private final ProductoService productoService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public VentaController(VentaService ventaService, ProductoService productoService) {
        this.ventaService = ventaService;
        this.productoService = productoService;
    }

    @GetMapping("/listado")
    public String listado(Model model, @RequestParam(required = false) String query) {
        List<Venta> ventas = (query != null && !query.trim().isEmpty())
            ? ventaService.buscar(query)
            : ventaService.listarTodas();

        model.addAttribute("ventas", ventas);
        model.addAttribute("query", query);
        return "ventas/listado";
    }

    @GetMapping("/nueva")
    public String nueva(Model model) {
        Venta venta = new Venta();
        venta.setCliente(new Cliente());
        venta.setDetalles(new ArrayList<>());

        model.addAttribute("venta", venta);
        model.addAttribute("productos", productoService.listarTodos());
        return "ventas/nueva";
    }

    @PostMapping("/guardar")
    public String guardar(
        @ModelAttribute Venta venta,
        @RequestParam("detallesJson") String detallesJson,
        RedirectAttributes flash
    ) {
        try {
            List<Map<String, Object>> detallesMap = objectMapper.readValue(
                detallesJson, new TypeReference<>() {}
            );

            if (detallesMap.isEmpty()) {
                flash.addFlashAttribute("alert", Alert.sweetAlertError("Debe agregar al menos un producto para registrar la venta."));
                return "redirect:/ventas/nueva";
            }

            List<DetalleVenta> detalles = new ArrayList<>();
            for (Map<String, Object> item : detallesMap) {
                DetalleVenta detalle = new DetalleVenta();
                Producto producto = new Producto();
                producto.setId(Integer.parseInt(item.get("productoId").toString()));

                detalle.setProducto(producto);
                detalle.setCantidad((Integer) item.get("cantidad"));
                detalle.setPrecioUnitario(Double.parseDouble(item.get("precio").toString()));

                detalles.add(detalle);
            }

            venta.setDetalles(detalles);

            for (DetalleVenta d : detalles) {
                d.setVenta(venta);
            }

            Venta ventaGuardada = ventaService.registrarVenta(venta);

            flash.addFlashAttribute("alert", Alert.sweetAlertSuccess("Venta registrada correctamente"));
            return "redirect:/ventas/factura/" + ventaGuardada.getId();
        } catch (Exception e) {
            e.printStackTrace();
            flash.addFlashAttribute("alert", Alert.sweetAlertError("Error al registrar la venta: " + e.getMessage()));
            return "redirect:/ventas/nueva";
        }
    }


    @GetMapping("/factura/{id}")
    public String factura(@PathVariable Integer id, Model model) {
        model.addAttribute("venta", ventaService.obtenerPorId(id));
        return "ventas/factura";
    }
}
