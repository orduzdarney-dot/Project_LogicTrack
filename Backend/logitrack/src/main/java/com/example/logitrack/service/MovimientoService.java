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
    private ProductoRepository productoRepository;

    public List<Movimiento> obtenerTodos() {
        return repository.findAll();
    }

    public Movimiento obtenerPorId(Long id) {
        return repository.findById(id).orElse(null);
    }

    public Movimiento registrarMovimiento(Movimiento movimiento) {
        // Lógica de negocio: actualizar el stock del producto
        Producto producto = movimiento.getProducto();

        if (movimiento.getTipo() == TipoMovimiento.ENTRADA) {
            producto.setStock(producto.getStock() + movimiento.getCantidad());
        } else if (movimiento.getTipo() == TipoMovimiento.SALIDA) {
            if (producto.getStock() < movimiento.getCantidad()) {
                throw new RuntimeException("No hay suficiente stock");
            }
            producto.setStock(producto.getStock() - movimiento.getCantidad());
        }
        // TRANSFERENCIA no cambia el stock total, solo mueve entre bodegas

        productoRepository.save(producto);

        return repository.save(movimiento);
    }

    public void eliminar(Long id) {
        repository.deleteById(id);
    }
}