package com.example.logitrack.service;

import com.example.logitrack.modelo.Movimiento;
import com.example.logitrack.modelo.Producto;
import com.example.logitrack.modelo.TipoMovimiento;
import com.example.logitrack.repository.MovimientoRepository;
import com.example.logitrack.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovimientoService {

    @Autowired
    private MovimientoRepository repository;

    @Autowired
    private ProductoRepository productoRepository; // Necesitamos esto para buscar el producto real

    public List<Movimiento> obtenerTodos() {
        return repository.findAll();
    }

    public Movimiento obtenerPorId(Long id) {
        return repository.findById(id).orElse(null);
    }

    public Movimiento registrarMovimiento(Movimiento movimiento) {
        // PASO 1: Obtener el ID del producto que viene en el JSON
        Long productoId = movimiento.getProducto().getId();

        // PASO 2: Buscar el producto REAL en la base de datos
        Producto productoReal = productoRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + productoId));

        // PASO 3: Modificar el stock del producto REAL
        int stockActual = productoReal.getStock() != null ? productoReal.getStock() : 0; // Seguridad por si es null

        if (movimiento.getTipo() == TipoMovimiento.ENTRADA) {
            productoReal.setStock(stockActual + movimiento.getCantidad());

        } else if (movimiento.getTipo() == TipoMovimiento.SALIDA) {
            if (stockActual < movimiento.getCantidad()) {
                throw new RuntimeException("No hay suficiente stock. Stock actual: " + stockActual);
            }
            productoReal.setStock(stockActual - movimiento.getCantidad());
        }
        // Si es TRANSFERENCIA, el stock total no cambia, solo cambia de bodega.

        // PASO 4: Guardar el producto actualizado en la base de datos
        productoRepository.save(productoReal);

        // PASO 5: Asignar el producto real al movimiento y guardar el movimiento
        movimiento.setProducto(productoReal);
        return repository.save(movimiento);
    }

    public void eliminar(Long id) {
        repository.deleteById(id);
    }
}