package com.example.logitrack.controller;

import com.example.logitrack.modelo.Auditoria;
import com.example.logitrack.service.AuditoriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auditorias")
@Tag(name = "Auditorías", description = "Consulta del historial de operaciones (INSERT, UPDATE, DELETE). Solo para usuarios con rol ADMIN")
public class AuditoriaController {

    @Autowired
    private AuditoriaService service;

    @Operation(summary = "Listar todas las auditorías",
            description = "Devuelve el historial completo de operaciones registradas automáticamente: "
                    + "tipo de operación, fecha, entidad afectada, valores anteriores/nuevos y usuario responsable. "
                    + "Solo para usuarios con rol ADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente"),
            @ApiResponse(responseCode = "401", description = "Token ausente o inválido"),
            @ApiResponse(responseCode = "403", description = "Sin permisos: se requiere rol ADMIN")
    })
    @GetMapping
    public ResponseEntity<List<Auditoria>> obtenerTodas() {
        return ResponseEntity.ok(service.obtenerTodos());
    }

    @Operation(summary = "Consultar una auditoría por ID",
            description = "Solo para usuarios con rol ADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Registro de auditoría encontrado"),
            @ApiResponse(responseCode = "401", description = "Token ausente o inválido"),
            @ApiResponse(responseCode = "403", description = "Sin permisos: se requiere rol ADMIN"),
            @ApiResponse(responseCode = "404", description = "No existe una auditoría con ese ID")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Auditoria> obtenerPorId(
            @Parameter(description = "ID del registro de auditoría", example = "1") @PathVariable Long id) {
        Auditoria auditoria = service.obtenerPorId(id);
        if (auditoria == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(auditoria);
    }
}