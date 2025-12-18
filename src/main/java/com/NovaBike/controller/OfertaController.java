
package com.NovaBike.controller;

import com.NovaBike.service.OfertaService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/ofertas")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class OfertaController {

    private final OfertaService ofertaService;

    public OfertaController(OfertaService ofertaService) {
        this.ofertaService = ofertaService;
    }

    @PostMapping("/enviar")
    public String enviarOfertas(
            @RequestParam String asunto,
            @RequestParam String mensaje,
            RedirectAttributes redirect) {

        ofertaService.enviarOfertasATodos(asunto, mensaje);
        redirect.addFlashAttribute("exito",
                "Ofertas enviadas correctamente");

        return "redirect:/usuario/listado";
    }
}
