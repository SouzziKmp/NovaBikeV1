/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.NovaBike.service;

import com.NovaBike.domain.EstadoFactura;
import com.NovaBike.domain.Factura;
import com.NovaBike.domain.ItemCarrito;
import com.NovaBike.domain.Producto;
import com.NovaBike.domain.Usuario;
import com.NovaBike.domain.Venta;
import com.NovaBike.repository.FacturaRepository;
import com.NovaBike.repository.ProductoRepository;
import com.NovaBike.repository.VentaRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Steven
 */
@Service
public class FacturaService {

    private final FacturaRepository facturaRepository;
    private final VentaRepository ventaRepository;
    private final ProductoRepository productoRepository;

    public FacturaService(
            FacturaRepository facturaRepository,
            VentaRepository ventaRepository,
            ProductoRepository productoRepository) {
        this.facturaRepository = facturaRepository;
        this.ventaRepository = ventaRepository;
        this.productoRepository = productoRepository;
    }

    @Transactional
    public Factura crearFacturaDesdeCarrito(
            Usuario usuario,
            List<ItemCarrito> carrito) {

        //Crear Factura
        Factura factura = new Factura();
        factura.setUsuario(usuario);
        factura.setEstado(EstadoFactura.ACTIVA);
        factura.setFechaCreacion(LocalDateTime.now());

        BigDecimal total = BigDecimal.ZERO;

        // Guardar Factura primero
        facturaRepository.save(factura);

        // Crear Ventas
        for (ItemCarrito item : carrito) {

            Producto producto = item.getProducto();

            Venta venta = new Venta();
            venta.setFactura(factura);
            venta.setProducto(producto);
            venta.setCantidad(item.getCantidad());
            venta.setPrecioHistorico(producto.getPrecio());
            venta.setFechaCreacion(LocalDateTime.now());

            ventaRepository.save(venta);

            // Calcular el total
            total = total.add(
                    producto.getPrecio()
                            .multiply(BigDecimal.valueOf(item.getCantidad()))
            );

            // Menejar Stock
            // producto.setStock(producto.getStock() - item.getCantidad());
            // productoRepository.save(producto);
        }

        // Guardar factura actual
        factura.setTotal(total);
        facturaRepository.save(factura);

        return factura;
    }
    
    @Transactional(readOnly = true)
public Factura getFacturaConVentas(Integer idFactura) {
    return facturaRepository.findByIdFacturaConDetalle(idFactura)
            .orElseThrow(() ->
                new RuntimeException("Factura no encontrada con id " + idFactura)
            );
}

}