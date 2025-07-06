package com.reptilg.controllers;

import com.reptilg.dtos.ResultadoResponse;
import com.reptilg.models.*;
import com.reptilg.services.*;
import com.reptilg.utils.Alert;

import java.util.Comparator;
import java.util.List;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/productos")
public class ProductoController {

    private final ProductoService productoService;
    private final CategoriaService categoriaService;
    private final ProveedorService proveedorService;
    private final PlataformaService plataformaService;

    public ProductoController(
        ProductoService productoService,
        CategoriaService categoriaService,
        ProveedorService proveedorService,
        PlataformaService plataformaService
    ) {
        this.productoService = productoService;
        this.categoriaService = categoriaService;
        this.proveedorService = proveedorService;
        this.plataformaService = plataformaService;
    }

    @GetMapping("/listado")
    public String listado(Model model,
                          @RequestParam(required = false) String query,
                          @RequestParam(required = false) String ordenStock,
                          @RequestParam(required = false) String ordenPrecio,
                          HttpSession session) {

        if (session.getAttribute("cuenta") == null)
            return "redirect:/login";

        List<Producto> productos = productoService.listarTodos();

        if (query != null && !query.trim().isEmpty()) {
            productos = productoService.buscar(query);
        }

        if (ordenStock != null) {
            if ("asc".equals(ordenStock)) {
                productos.sort(Comparator.comparing(Producto::getStock));
            } else if ("desc".equals(ordenStock)) {
                productos.sort(Comparator.comparing(Producto::getStock).reversed());
            }
        } else if (ordenPrecio != null) {
            if ("asc".equals(ordenPrecio)) {
                productos.sort(Comparator.comparing(Producto::getPrecio));
            } else if ("desc".equals(ordenPrecio)) {
                productos.sort(Comparator.comparing(Producto::getPrecio).reversed());
            }
        }

        model.addAttribute("productos", productos);
        model.addAttribute("query", query);
        model.addAttribute("ordenStock", ordenStock);
        model.addAttribute("ordenPrecio", ordenPrecio);

        return "productos/listado";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model, HttpSession session) {
        if (session.getAttribute("cuenta") == null)
            return "redirect:/login";

        Producto producto = new Producto();
        model.addAttribute("producto", producto);
        model.addAttribute("categorias", categoriaService.listarTodos());
        model.addAttribute("proveedores", proveedorService.listarTodos());
        model.addAttribute("plataformas", plataformaService.listarTodos());
        model.addAttribute("productoService", productoService);

        return "productos/nuevo";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Producto producto, RedirectAttributes flash, HttpSession session) {
        if (session.getAttribute("cuenta") == null)
            return "redirect:/login";

        producto.setDetallesVenta(null);
        String usuario = (String) session.getAttribute("nombreCompleto");
        productoService.guardar(producto, usuario); // 👈 pasa el usuario

        flash.addFlashAttribute("success", "¡Producto registrado!");
        return "redirect:/productos/listado";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Integer id, Model model, HttpSession session) {
        if (session.getAttribute("cuenta") == null)
            return "redirect:/login";

        model.addAttribute("producto", productoService.obtenerPorId(id));
        model.addAttribute("categorias", categoriaService.listarTodos());
        model.addAttribute("proveedores", proveedorService.listarTodos());
        model.addAttribute("plataformas", plataformaService.listarTodos());
        return "productos/edicion";
    }

    @GetMapping("/detalle/{id}")
    public String detalle(@PathVariable Integer id, Model model, HttpSession session) {
        if (session.getAttribute("cuenta") == null)
            return "redirect:/login";

        model.addAttribute("producto", productoService.obtenerPorId(id));
        return "productos/detalle";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id, RedirectAttributes flash, HttpSession session) {
        if (session.getAttribute("cuenta") == null)
            return "redirect:/login";

        String usuario = (String) session.getAttribute("nombreCompleto");
        ResultadoResponse response = productoService.eliminar(id, usuario); // 👈 pasa el usuario

        if (response.isSuccess()) {
            flash.addFlashAttribute("alert", Alert.sweetToast(response.getMensaje(), "success", 3000));
        } else {
            flash.addFlashAttribute("alert", Alert.sweetAlertError(response.getMensaje()));
        }

        return "redirect:/productos/listado";
    }
}
