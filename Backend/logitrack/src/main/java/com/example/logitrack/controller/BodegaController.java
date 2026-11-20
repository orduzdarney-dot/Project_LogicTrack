package com.example.logitrack.controller;

import com.example.logitrack.modelo.Bodega;
import com.example.logitrack.service.BodegaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bodegas")
@Tag(name = "Bodegas", description = "Gestión de bodegas: ubicación, capacidad y encargado")
public class BodegaController {

    @Autowired
    private BodegaService service;

    @Operation(summary = "Listar todas las bodegas",
            description = "Devuelve todas las bodegas registradas con su ubicación, capacidad y encargado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente"),
            @ApiResponse(responseCode = "401", description = "Token ausente o inválido")
    })
    @GetMapping
    public ResponseEntity<List<Bodega>> obtenerTodos() {
        return ResponseEntity.ok(service.obtenerTodos());
    }

    @Operation(summary = "Consultar una bodega por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Bodega encontrada"),
            @ApiResponse(responseCode = "401", description = "Token ausente o inválido"),
            @ApiResponse(responseCode = "404", description = "No existe una bodega con ese ID")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Bodega> obtenerPorId(
            @Parameter(description = "ID de la bodega", example = "1") @PathVariable Long id) {
        Bodega bodega = service.obtenerPorId(id);
        if (bodega == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(bodega);
    }

    @Operation(summary = "Crear una bodega",
            description = "Registra una nueva bodega. Nombre, ubicación y capacidad son obligatorios; "
                    + "el encargado es opcional.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = @ExampleObject(name = "Bodega", value = """
                    {
                      "nombre": "Bodega Central",
                      "ubicacion": "Bucaramanga, Santander",
                      "capacidad": 5000,
                      "encargado": "Juan Pérez"
                    }
                    """)))
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Bodega creada correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o campos obligatorios faltantes"),
            @ApiResponse(responseCode = "401", description = "Token ausente o inválido")
    })
    @PostMapping
    public ResponseEntity<Bodega> crear(@RequestBody Bodega bodega) {
        Bodega nueva = service.guardar(bodega);
        return ResponseEntity.status(201).body(nueva);
    }

    @Operation(summary = "Actualizar una bodega",
            description = "Reemplaza los datos de la bodega indicada.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = @ExampleObject(name = "Bodega", value = """
                    {
                      "nombre": "Bodega Central",
                      "ubicacion": "Bucaramanga, Santander",
                      "capacidad": 8000,
                      "encargado": "María Gómez"
                    }
                    """)))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Bodega actualizada correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "Token ausente o inválido"),
            @ApiResponse(responseCode = "404", description = "No existe una bodega con ese ID")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Bodega> actualizar(
            @Parameter(description = "ID de la bodega a actualizar", example = "1") @PathVariable Long id,
            @RequestBody Bodega bodega) {
        Bodega existente = service.obtenerPorId(id);
        if (existente == null) {
            return ResponseEntity.notFound().build();
        }
        bodega.setId(id);
        return ResponseEntity.ok(service.guardar(bodega));
    }

    @Operation(summary = "Eliminar una bodega", description = "Solo para usuarios con rol ADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Bodega eliminada"),
            @ApiResponse(responseCode = "401", description = "Token ausente o inválido"),
            @ApiResponse(responseCode = "403", description = "Sin permisos: se requiere rol ADMIN"),
            @ApiResponse(responseCode = "404", description = "No existe una bodega con ese ID")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID de la bodega a eliminar", example = "1") @PathVariable Long id) {
        if (service.obtenerPorId(id) == null) {
            return ResponseEntity.notFound().build();
        }
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}