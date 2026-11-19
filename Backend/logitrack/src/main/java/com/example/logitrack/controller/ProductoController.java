package com.example.logitrack.controller;

import com.example.logitrack.modelo.Producto;
import com.example.logitrack.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/productos")
public class ProductoController {

    @Autowired
    private ProductoService service;

    @GetMapping
    public ResponseEntity<List<Producto>> obtenerTodos() {
        return ResponseEntity.ok(service.obtenerTodos());
    }

    //  EL CONTROLLER YA NO VALIDA NULL, SOLO DELEGA AL SERVICE
    @GetMapping("/{id}")
    public ResponseEntity<Producto> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<Producto> crear(@RequestBody Producto producto) {
        Producto nuevo = service.guardar(producto);
        return ResponseEntity.status(201).body(nuevo);
    }

    // ✅ EL CONTROLLER YA NO VALIDA NULL, EL SERVICE LANZARÁ LA EXCEPCIÓN SI NO EXISTE
    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizar(@PathVariable Long id, @RequestBody Producto producto) {
        // Esto lanzará RecursoNoEncontradoException si no existe, y el Manejador Global lo atrapará
        service.obtenerPorId(id);
        producto.setId(id);
        return ResponseEntity.ok(service.guardar(producto));
    }

    // ✅ EL CONTROLLER YA NO VALIDA NULL, EL SERVICE LANZARÁ LA EXCEPCIÓN
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id); // Esto lanzará la excepción si no existe
        return ResponseEntity.noContent().build();
    }

    // Endpoint especial: productos con stock bajo
    @GetMapping("/stock-bajo")
    public ResponseEntity<List<Producto>> obtenerStockBajo() {
        return ResponseEntity.ok(service.obtenerStockBajo());
    }
}