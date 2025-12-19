package com.NovaBike.controller;

import com.NovaBike.domain.FiltroProducto;
import com.NovaBike.domain.Producto;
import com.NovaBike.service.ProductoService;
import com.NovaBike.service.FavoritoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequestMapping("/producto")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private FavoritoService favoritoService;

    @GetMapping("/listado")
    public String listado(
            @RequestParam(name = "q", required = false) String q,
            @RequestParam(name = "favoritos", required = false, defaultValue = "false") boolean verFavoritos,
            FiltroProducto filtro,
            Model model) {

        List<Producto> productos = productoService.getProductos();

        // Filtrar por favoritos
        if (verFavoritos) {
            List<Integer> idsFavoritos = favoritoService.getFavoritos();
            productos = productos.stream()
                    .filter(p -> idsFavoritos.contains(p.getId()))
                    .toList();
        }

        // Búsqueda por nombre
        if (q != null && !q.isEmpty()) {
            String qLower = q.toLowerCase();
            productos = productos.stream()
                    .filter(p -> p.getNombre() != null
                    && p.getNombre().toLowerCase().contains(qLower))
                    .toList();
            model.addAttribute("q", q);
        }

        // Filtro por rango de precio
        if (filtro.getMin() != null && filtro.getMax() != null) {
            productos = productos.stream()
                    .filter(p -> p.getPrecio() != null
                    && p.getPrecio().compareTo(filtro.getMin()) >= 0
                    && p.getPrecio().compareTo(filtro.getMax()) <= 0)
                    .toList();

            model.addAttribute("min", filtro.getMin());
            model.addAttribute("max", filtro.getMax());
        }

        model.addAttribute("productos", productos);
        model.addAttribute("totalProductos", productos.size());
        model.addAttribute("favoritos", favoritoService.getFavoritos());

        return "producto/listado";
    }

    @GetMapping("/agregar")
    public String agregar(Model model) {
        model.addAttribute("producto", new Producto());
        model.addAttribute("productos", productoService.getProductos());
        return "producto/agregar";
    }

    @PostMapping("/guardar")
    public String guardar(
            @ModelAttribute Producto producto,
            @RequestParam("imagenFile") MultipartFile imageFile) {

        productoService.save(producto, imageFile);
        return "redirect:/producto/agregar";
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

    @GetMapping("/destacados")
    public String destacados(Model model) {
        var productos = productoService.getDestacados();
        model.addAttribute("productos", productos);
        return "producto/destacados";
    }

    @GetMapping("/modificar/{id}")
    public String modificar(@PathVariable Integer id, Model model) {
        Producto producto = productoService.getProducto(id);
        model.addAttribute("producto", producto);
        model.addAttribute("productos", productoService.getProductos());
        return "producto/agregar";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id) {
        productoService.delete(id);
        return "redirect:/producto/agregar";
    }

    @GetMapping("/reporte")   // 👈 AQUÍ EL CAMBIO
    public String reporteStock(Model model) {
        model.addAttribute("productos", productoService.getProductos());
        return "producto/reporte";
    }

    @GetMapping("/producto/listado")
    public String listado(Model model) {
        model.addAttribute("productos", productoService.getProductos());
        return "producto/listado";
    }

}
