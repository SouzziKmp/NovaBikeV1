package com.NovaBike.controller;

import com.NovaBike.domain.Factura;
import com.NovaBike.domain.ItemCarrito;
import com.NovaBike.domain.Producto;
import com.NovaBike.domain.Usuario;
import com.NovaBike.service.FacturaService;
import com.NovaBike.service.ProductoService;
import com.NovaBike.service.UsuarioService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.security.core.Authentication;

@Controller
@RequestMapping("/carrito")
@SessionAttributes("carrito")

public class CarritoController {

    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private FacturaService facturaService;
    
    @Autowired
    private ProductoService productoService;

    //  Si no existe el carrito, se crea uno nuevo
    @ModelAttribute("carrito")
    public List<ItemCarrito> inicializarCarrito() {
        return new ArrayList<>();
    }

    //  Agregar producto al carrito
    @GetMapping("/agregar/{id}")
    public String agregarAlCarrito(@PathVariable Long id,
            @ModelAttribute("carrito") List<ItemCarrito> carrito) {

        Producto producto = productoService.getProducto(id.intValue());

        if (producto != null) {
            ItemCarrito existente = carrito.stream()
                    .filter(i -> i.getProducto().getId().equals(producto.getId()))
                    .findFirst()
                    .orElse(null);

            if (existente == null) {
                ItemCarrito item = new ItemCarrito();
                item.setProducto(producto);
                item.setCantidad(1);
                carrito.add(item);
            } else {
                existente.setCantidad(existente.getCantidad() + 1);
            }
        }
        return "redirect:/carrito/ver";
    }

    //  Mostrar carrito
    @GetMapping("/ver")
    public String verCarrito(
            @ModelAttribute("carrito") List<ItemCarrito> carrito,
            Model model) {

        BigDecimal total = carrito.stream()
                .map(i -> i.getProducto().getPrecio()
                .multiply(BigDecimal.valueOf(i.getCantidad())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("carrito", carrito);
        model.addAttribute("total", total);

        return "producto/pedidos";
    }

    // Eliminar producto del carrito
    @GetMapping("/eliminar/{id}")
    public String eliminarDelCarrito(@PathVariable("id") Long id,
            @ModelAttribute("carrito") List<ItemCarrito> carrito) {

        carrito.removeIf(item
                -> item.getProducto().getId().longValue() == id.longValue()
        );

        return "redirect:/carrito/ver";
    }
    

    @GetMapping("/confirmar")
    public String confirmarCompra(
            @ModelAttribute("carrito") List<ItemCarrito> carrito,
            Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        if (carrito.isEmpty()) {
            return "redirect:/carrito/ver";
        }

        String username = authentication.getName();

        //  Buscar tu entidad Usuario real
        Usuario usuario = usuarioService
        .getUsuarioPorUsername(username)
        .orElseThrow(() -> new RuntimeException(
                "Usuario no encontrado: " + username
        ));

        Factura factura = facturaService.crearFacturaDesdeCarrito(usuario, carrito);
        carrito.clear();

        return "redirect:/factura/ver/" + factura.getIdFactura();
    }
}
