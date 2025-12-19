package com.NovaBike.controller;

import com.NovaBike.domain.Factura;
import com.NovaBike.service.FacturaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/factura")
public class FacturaController {

    @Autowired
    private FacturaService facturaService;

    @GetMapping("/ver/{id}")
    public String verFactura(@PathVariable Integer id, Model model) {

        Factura factura = facturaService.getFacturaConVentas(id);
        model.addAttribute("factura", factura);

        return "factura/ver"; // templates/factura/ver.html
    }
}
