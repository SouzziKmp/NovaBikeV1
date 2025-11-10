package com.NovaBike.service;

import com.NovaBike.domain.Producto;
import com.NovaBike.repository.ProductoRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    @Transactional(readOnly = true)
    public List<Producto> getProductos() {
        return productoRepository.findAll();
    }
    
     @Transactional
    public void save(Producto producto) {
        productoRepository.save(producto);
    }
    
}
