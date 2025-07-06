package com.reptilg.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reptilg.models.*;
import com.reptilg.services.ClienteService;
import com.reptilg.services.ProductoService;
import com.reptilg.services.VentaService;
import com.reptilg.utils.Alert;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.*;

@Controller
@RequestMapping("/ventas")
public class VentaController {

    private final VentaService ventaService;
    private final ProductoService productoService;
    private final ClienteService clienteService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public VentaController(VentaService ventaService,
                           ProductoService productoService,
                           ClienteService clienteService) {
        this.ventaService = ventaService;
        this.productoService = productoService;
        this.clienteService = clienteService;
    }

    @GetMapping("/listado")
    public String listado(Model model,
                          @RequestParam(required = false) String query,
                          @RequestParam(required = false) String fechaInicio,
                          @RequestParam(required = false) String fechaFin,
                          @RequestParam(required = false) String ordenMonto,
                          HttpSession session) {
        if (session.getAttribute("cuenta") == null)
            return "redirect:/login";

        List<Venta> ventas = (query != null && !query.trim().isEmpty())
            ? ventaService.buscar(query) : ventaService.listarTodas();

        if ((fechaInicio != null && !fechaInicio.isEmpty()) || (fechaFin != null && !fechaFin.isEmpty())) {
            LocalDateTime inicio = (fechaInicio != null && !fechaInicio.isEmpty())
                    ? LocalDateTime.parse(fechaInicio + "T00:00:00")
                    : LocalDateTime.of(2000, 1, 1, 0, 0);
            LocalDateTime fin = (fechaFin != null && !fechaFin.isEmpty())
                    ? LocalDateTime.parse(fechaFin + "T23:59:59")
                    : LocalDateTime.now();
            ventas = ventaService.filtrarPorFecha(ventas, inicio, fin);
        }

        if ("asc".equals(ordenMonto)) {
            ventas.sort(Comparator.comparing(Venta::getTotal));
        } else if ("desc".equals(ordenMonto)) {
            ventas.sort(Comparator.comparing(Venta::getTotal).reversed());
        }

        model.addAttribute("ventas", ventas);
        model.addAttribute("query", query);
        model.addAttribute("fechaInicio", fechaInicio);
        model.addAttribute("fechaFin", fechaFin);
        model.addAttribute("ordenMonto", ordenMonto);

        return "ventas/listado";
    }

    @GetMapping("/nueva")
    public String nueva(Model model, HttpSession session) {
        if (session.getAttribute("cuenta") == null)
            return "redirect:/login";

        Venta venta = new Venta();
        venta.setCliente(new Cliente());
        venta.setDetalles(new ArrayList<>());

        model.addAttribute("venta", venta);
        model.addAttribute("productos", productoService.listarTodos());
        return "ventas/nueva";
    }

    @GetMapping("/buscarCliente")
    @ResponseBody
    public ResponseEntity<?> buscarClientePorDni(@RequestParam String dni, HttpSession session) {
        if (session.getAttribute("cuenta") == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Sesión no iniciada");

        try {
            Optional<Cliente> cliente = clienteService.buscarPorDni(dni);
            return cliente.isPresent()
                    ? ResponseEntity.ok(cliente.get())
                    : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al buscar cliente");
        }
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Venta venta,
                          @RequestParam("detallesJson") String detallesJson,
                          RedirectAttributes flash,
                          HttpSession session) {
        if (session.getAttribute("cuenta") == null)
            return "redirect:/login";

        try {
            List<Map<String, Object>> detallesMap = objectMapper.readValue(detallesJson, new TypeReference<>() {});
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
            detalles.forEach(d -> d.setVenta(venta));

            String usuario = (String) session.getAttribute("nombreCompleto");
            Venta ventaGuardada = ventaService.registrarVenta(venta, usuario); // 👈 se registra con nombre

            flash.addFlashAttribute("alert", Alert.sweetAlertSuccess("Venta registrada correctamente"));
            return "redirect:/ventas/factura/" + ventaGuardada.getId();

        } catch (Exception e) {
            e.printStackTrace();
            flash.addFlashAttribute("alert", Alert.sweetAlertError("Error al registrar la venta: " + e.getMessage()));
            return "redirect:/ventas/nueva";
        }
    }

    @GetMapping("/factura/{id}")
    public String factura(@PathVariable Integer id, Model model, HttpSession session) {
        if (session.getAttribute("cuenta") == null)
            return "redirect:/login";

        model.addAttribute("venta", ventaService.obtenerPorId(id));
        return "ventas/factura";
    }
}
