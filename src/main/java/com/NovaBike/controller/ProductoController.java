package com.NovaBike.controller;
 
import com.NovaBike.domain.Producto;

import com.NovaBike.service.ProductoService;

import com.NovaBike.service.FavoritoService;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.*;
 
import java.util.List;
 
@Controller

@RequestMapping("/producto")

public class ProductoController {
 
    @Autowired

    private ProductoService productoService;
 
    @Autowired

    private FavoritoService favoritoService;
 
 
    @GetMapping("/listado")

    public String listado(@RequestParam(name = "q", required = false) String q,

                          @RequestParam(name = "favoritos", required = false) boolean verFavoritos,

                          Model model) {
 
        List<Producto> productos = productoService.getProductos();
 


        if (verFavoritos) {

            List<Integer> idsFavoritos = favoritoService.getFavoritos();

            productos = productos.stream()

                    .filter(p -> idsFavoritos.contains(p.getId()))

                    .toList();

        }
 
        if (q != null && !q.isEmpty()) {

            productos = productos.stream()

                    .filter(p -> p.getNombre().toLowerCase().contains(q.toLowerCase()))

                    .toList();

            model.addAttribute("q", q);

        }
 
        model.addAttribute("productos", productos);

        model.addAttribute("totalProductos", productos.size());

        model.addAttribute("favoritos", favoritoService.getFavoritos());

        return "producto/listado";

    }
    
    
     @GetMapping("/agregar")
    public String agregar(Model model) {
        model.addAttribute("producto", new Producto());
        return "producto/agregar";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Producto producto) {
        productoService.save(producto);
        return "redirect:/producto/listado";
    }
 
    @PostMapping("/favorito/{id}")

    public String toggleFavorito(@PathVariable Integer id,

                                 @RequestParam(required = false) String q) {

        favoritoService.toggleFavorito(id);

        String redirectUrl = "/producto/listado";

        if (q != null && !q.isEmpty()) {

            redirectUrl += "?q=" + q;

        }

        return "redirect:" + redirectUrl;

    }

}
