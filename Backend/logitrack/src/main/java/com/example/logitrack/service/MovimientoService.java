package com.example.logitrack.service;

import com.example.logitrack.modelo.*;
import com.example.logitrack.repository.MovimientoRepository;
import com.example.logitrack.repository.ProductoRepository;
import com.example.logitrack.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MovimientoService {

    @Autowired
    private MovimientoRepository repository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<Movimiento> obtenerTodos() {
        return repository.findAll();
    }

    public Movimiento obtenerPorId(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Transactional
    public Movimiento registrarMovimiento(Movimiento movimiento) {
        // 1. Validar que venga el producto con su ID
        if (movimiento.getProducto() == null || movimiento.getProducto().getId() == null) {
            throw new RuntimeException("El producto es obligatorio");
        }

        Producto productoReal = productoRepository.findById(movimiento.getProducto().getId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + movimiento.getProducto().getId()));

        // 2. Obtener el usuario AUTOMÁTICAMENTE desde el token JWT (Seguridad)
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuarioReal = usuarioRepository.findByNombre(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + username));

        // 3. Asignar datos que el frontend no debe enviar
        movimiento.setFecha(LocalDateTime.now());
        movimiento.setUsuario(usuarioReal);
        movimiento.setProducto(productoReal);

        // 4. Lógica de Stock
        int stockActual = productoReal.getStock() != null ? productoReal.getStock() : 0;

        if (movimiento.getTipo() == TipoMovimiento.ENTRADA) {
            productoReal.setStock(stockActual + movimiento.getCantidad());
        } else if (movimiento.getTipo() == TipoMovimiento.SALIDA) {
            if (stockActual < movimiento.getCantidad()) {
                throw new RuntimeException("No hay suficiente stock. Stock actual: " + stockActual);
            }
            productoReal.setStock(stockActual - movimiento.getCantidad());
        }


        productoRepository.save(productoReal);

        // 5. Guardar el movimiento
        return repository.save(movimiento);
    }

    public void eliminar(Long id) {
        repository.deleteById(id);
    }
}