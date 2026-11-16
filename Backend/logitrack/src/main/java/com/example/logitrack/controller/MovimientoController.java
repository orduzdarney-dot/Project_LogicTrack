package com.example.logitrack.controller;

import com.example.logitrack.modelo.Movimiento;
import com.example.logitrack.service.MovimientoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/movimientos")
public class MovimientoController {

    @Autowired
    private MovimientoService service;

    @GetMapping
    public ResponseEntity<List<Movimiento>> obtenerTodos() {
        return ResponseEntity.ok(service.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Movimiento> obtenerPorId(@PathVariable Long id) {
        Movimiento movimiento = service.obtenerPorId(id);
        if (movimiento == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(movimiento);
    }

    @PostMapping
    public ResponseEntity<Movimiento> registrar(@RequestBody Movimiento movimiento) {
        Movimiento nuevo = service.registrarMovimiento(movimiento);
        return ResponseEntity.status(201).body(nuevo);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (service.obtenerPorId(id) == null) {
            return ResponseEntity.notFound().build();
        }
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}