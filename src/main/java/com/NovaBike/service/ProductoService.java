package com.NovaBike.service;

import com.NovaBike.domain.Producto;
import com.NovaBike.repository.ProductoRepository;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private FirebaseStorageService firebaseStorageService;

    @Transactional(readOnly = true)
    public List<Producto> getProductos() {
        return productoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Producto getProducto(Integer id) {
        return productoRepository.findById(id).orElse(null);
    }

    @Transactional(readOnly = true)
    public List<Producto> getDestacados() {
        return productoRepository.findByDestacadoTrue();
    }

    @Transactional
    public void save(Producto producto, MultipartFile imageFile) {

        producto = productoRepository.save(producto);

        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                String rutaImagen = firebaseStorageService.uploadImage(
                        imageFile,
                        "producto",
                        producto.getId()
                );
                producto.setRutaImagen(rutaImagen);
                productoRepository.save(producto);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public BigDecimal calcularPrecioConDescuento(Producto p) {
        if (p.getDescuento() <= 0) {
            return p.getPrecio();
        }

        BigDecimal porcentaje = BigDecimal
                .valueOf(p.getDescuento())
                .divide(BigDecimal.valueOf(100));

        BigDecimal rebaja = p.getPrecio().multiply(porcentaje);

        return p.getPrecio().subtract(rebaja);
    }

    @Transactional
    public void delete(Integer id) {
        productoRepository.deleteById(id);
    }

    @Transactional
    public void descontarStock(Integer idProducto, int cantidad) {

        Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new RuntimeException("Producto no existe"));

        if (producto.getStock() < cantidad) {
            throw new RuntimeException(
                    "Stock insuficiente para el producto: " + producto.getNombre()
            );
        }

        producto.setStock(producto.getStock() - cantidad);
        productoRepository.save(producto);
    }
}
