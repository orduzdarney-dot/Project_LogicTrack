package com.example.logitrack.service;

import com.example.logitrack.exception.RecursoNoEncontradoException; // ← IMPORTANTE
import com.example.logitrack.modelo.Producto;
import com.example.logitrack.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository repository;

    public List<Producto> obtenerTodos() {
        return repository.findAll();
    }

    // ✅ AHORA LANZA LA EXCEPCIÓN EN LUGAR DE DEVOLVER NULL
    public Producto obtenerPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado con ID: " + id));
    }

    public Producto guardar(Producto producto) {
        return repository.save(producto);
    }

    public void eliminar(Long id) {
        // Opcional: validar que exista antes de borrar para lanzar la excepción también aquí
        if (!repository.existsById(id)) {
            throw new RecursoNoEncontradoException("Producto no encontrado con ID: " + id);
        }
        repository.deleteById(id);
    }

    // Lógica de negocio: productos con stock bajo
    public List<Producto> obtenerStockBajo() {
        return repository.findAll().stream()
                .filter(p -> p.getStock() < 10)
                .toList();
    }
}