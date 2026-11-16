package com.example.logitrack.controller;

import com.example.logitrack.modelo.Auditoria;
import com.example.logitrack.service.AuditoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auditorias")
public class AuditoriaController {

    @Autowired
    private AuditoriaService service;

    @GetMapping
    public ResponseEntity<List<Auditoria>> obtenerTodas() {
        return ResponseEntity.ok(service.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Auditoria> obtenerPorId(@PathVariable Long id) {
        Auditoria auditoria = service.obtenerPorId(id);
        if (auditoria == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(auditoria);
    }
}