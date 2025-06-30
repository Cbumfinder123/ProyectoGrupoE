package com.reptilg.controllers;

import com.reptilg.dtos.ResultadoResponse;
import com.reptilg.models.*;
import com.reptilg.services.*;
import com.reptilg.utils.Alert;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
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
                         HttpServletRequest request) {
        
        List<Producto> productos;
        if (query != null && !query.trim().isEmpty()) {
            productos = productoService.buscar(query);
            model.addAttribute("query", query);  
        } else {
            productos = productoService.listarTodos();
        }
        
        model.addAttribute("productos", productos);
        return "productos/listado";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        Producto producto = new Producto();
        model.addAttribute("producto", producto);
        model.addAttribute("categorias", categoriaService.listarTodos());
        model.addAttribute("proveedores", proveedorService.listarTodos());
        model.addAttribute("plataformas", plataformaService.listarTodos());
        

        model.addAttribute("productoService", productoService);
        
        return "productos/nuevo";
    }
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Producto producto, RedirectAttributes flash) {
        producto.setDetallesVenta(null); // ✨ Esto corta la relación y evita el error
        productoService.guardar(producto);
        flash.addFlashAttribute("success", "¡Producto registrado!");
        return "redirect:/productos/listado";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Integer id, Model model) {
        model.addAttribute("producto", productoService.obtenerPorId(id));
        model.addAttribute("categorias", categoriaService.listarTodos());
        model.addAttribute("proveedores", proveedorService.listarTodos());
        model.addAttribute("plataformas", plataformaService.listarTodos());
        return "productos/edicion";
    }

    @GetMapping("/detalle/{id}")
    public String detalle(@PathVariable Integer id, Model model) {
        model.addAttribute("producto", productoService.obtenerPorId(id));
        return "productos/detalle";
    }
    
 
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id, RedirectAttributes flash) {
        ResultadoResponse response = productoService.eliminar(id);
        
        if (response.isSuccess()) {
            flash.addFlashAttribute("alert", 
                Alert.sweetToast(response.getMensaje(), "success", 3000));
        } else {
            flash.addFlashAttribute("alert", 
                Alert.sweetAlertError(response.getMensaje()));
        }
        
        return "redirect:/productos/listado";
    }
}