package com.reptilg.controllers;

import com.reptilg.dtos.AutenticacionFilter;
import com.reptilg.models.Usuario;
import com.reptilg.repositories.IUsuarioRepository;
import com.reptilg.services.AutenticacionService;
import com.reptilg.utils.Alert;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class LoginController {

    private final AutenticacionService autenticacionService;
    private final IUsuarioRepository usuarioRepository;

    @GetMapping({ "/", "/login" })
    public String login(Model model) {
        model.addAttribute("filter", new AutenticacionFilter());
        return "login";
    }

    @PostMapping("/iniciar-sesion")
    public String iniciarSesion(@ModelAttribute AutenticacionFilter filter,
                                 HttpSession session,
                                 Model model,
                                 RedirectAttributes flash) {

        Usuario usuario = autenticacionService.autenticar(filter);

        if (usuario == null) {
            model.addAttribute("filter", new AutenticacionFilter());
            model.addAttribute("alert", Alert.sweetAlertError("Usuario o contraseña incorrectos"));
            return "login";
        }

        session.setAttribute("idUsuario", usuario.getId());
        session.setAttribute("nombreCompleto", usuario.getNombreCompleto());
        session.setAttribute("cuenta", usuario.getUsuario());

        flash.addFlashAttribute("alert", Alert.sweetToast("Bienvenido " + usuario.getNombreCompleto(), "success", 3000));
        return "redirect:/home";
    }

    @GetMapping("/registro")
    public String mostrarFormularioRegistro(Model model) {
        model.addAttribute("nuevoUsuario", new Usuario()); 
        return "registro";
    }

    @PostMapping("/registrarse")
    public String registrarUsuario(@ModelAttribute("nuevoUsuario") Usuario usuario,
                                   RedirectAttributes flash) {
        usuario.setEstado(true);
        usuario.setTipo(2); // usuario regular
        usuarioRepository.save(usuario);

        flash.addFlashAttribute("alert", Alert.sweetToast("Cuenta registrada correctamente", "success", 3000));
        return "redirect:/login";
    }


    @GetMapping("/home")
    public String home(HttpSession session) {
        if (session.getAttribute("cuenta") == null)
            return "redirect:/login";
        return "home";
    }

    @GetMapping("/cerrar-sesion")
    public String cerrarSesion(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
