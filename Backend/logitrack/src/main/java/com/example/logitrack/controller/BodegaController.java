package com.example.logitrack.controller;

import com.example.logitrack.modelo.Bodega;
import com.example.logitrack.service.BodegaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bodegas")
public class BodegaController {

    @Autowired
    private BodegaService service;

    @GetMapping
    public ResponseEntity<List<Bodega>> obtenerTodos() {
        return ResponseEntity.ok(service.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Bodega> obtenerPorId(@PathVariable Long id) {
        Bodega bodega = service.obtenerPorId(id);
        if (bodega == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(bodega);
    }

    @PostMapping
    public ResponseEntity<Bodega> crear(@RequestBody Bodega bodega) {
        Bodega nueva = service.guardar(bodega);
        return ResponseEntity.status(201).body(nueva);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Bodega> actualizar(@PathVariable Long id, @RequestBody Bodega bodega) {
        Bodega existente = service.obtenerPorId(id);
        if (existente == null) {
            return ResponseEntity.notFound().build();
        }
        bodega.setId(id);
        return ResponseEntity.ok(service.guardar(bodega));
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