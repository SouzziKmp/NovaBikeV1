package com.NovaBike.controller;

import com.NovaBike.service.PasswordService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class PasswordController {

    private final PasswordService passwordService;

    public PasswordController(PasswordService passwordService) {
        this.passwordService = passwordService;
    }

    // Muestra la página de "olvidé mi contraseña"
    @GetMapping("/forgot-password")
    public String mostrarFormulario() {
        return "forgot-password";
    }

    // Procesa el formulario
    @PostMapping("/forgot-password")
    public String procesarSolicitud(@RequestParam("email") String email) {
        passwordService.enviarCorreoRecuperacion(email);
        return "redirect:/login?success";
    }

    //Enviar el enlace hacia el HTML
    @GetMapping("/reset-password")
    public String mostrarResetPassword(@RequestParam("token") String token, Model model) {
        model.addAttribute("token", token);
        return "reset-password";
    }

    // Proceso de cambio de contraseña
    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam String token,
            @RequestParam String password) {
        passwordService.restablecerPassword(token, password);
        return "redirect:/login?passwordChanged";
    }
}
