package com.example.logitrack.controller;

import com.example.logitrack.modelo.Movimiento;
import com.example.logitrack.service.MovimientoService;
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
@RequestMapping("/movimientos")
@Tag(name = "Movimientos", description = "Registro de entradas, salidas y transferencias de inventario")
public class MovimientoController {

    @Autowired
    private MovimientoService service;

    @Operation(summary = "Listar todos los movimientos")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente"),
            @ApiResponse(responseCode = "401", description = "Token ausente o inválido")
    })
    @GetMapping
    public ResponseEntity<List<Movimiento>> obtenerTodos() {
        return ResponseEntity.ok(service.obtenerTodos());
    }

    @Operation(summary = "Consultar un movimiento por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Movimiento encontrado"),
            @ApiResponse(responseCode = "401", description = "Token ausente o inválido"),
            @ApiResponse(responseCode = "404", description = "No existe un movimiento con ese ID")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Movimiento> obtenerPorId(
            @Parameter(description = "ID del movimiento", example = "1") @PathVariable Long id) {
        Movimiento movimiento = service.obtenerPorId(id);
        if (movimiento == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(movimiento);
    }

    @Operation(summary = "Registrar un movimiento de inventario",
            description = "El usuario y la fecha se asignan automáticamente desde el token. "
                    + "ENTRADA suma stock, SALIDA lo resta (falla si no hay suficiente).")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "Entrada", value = """
                            {
                              "tipo": "ENTRADA",
                              "cantidad": 10,
                              "producto": { "id": 1 },
                              "bodegaDestino": { "id": 1 }
                            }
                            """),
                    @ExampleObject(name = "Salida", value = """
                            {
                              "tipo": "SALIDA",
                              "cantidad": 5,
                              "producto": { "id": 1 },
                              "bodegaOrigen": { "id": 1 }
                            }
                            """),
                    @ExampleObject(name = "Transferencia", value = """
                            {
                              "tipo": "TRANSFERENCIA",
                              "cantidad": 10,
                              "producto": { "id": 1 },
                              "bodegaOrigen": { "id": 1 },
                              "bodegaDestino": { "id": 2 }
                            }
                            """)
            }))
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Movimiento registrado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos, producto inexistente o stock insuficiente"),
            @ApiResponse(responseCode = "401", description = "Token ausente o inválido")
    })
    @PostMapping
    public ResponseEntity<Movimiento> registrar(@RequestBody Movimiento movimiento) {
        Movimiento nuevo = service.registrarMovimiento(movimiento);
        return ResponseEntity.status(201).body(nuevo);
    }

    @Operation(summary = "Eliminar un movimiento")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Movimiento eliminado"),
            @ApiResponse(responseCode = "401", description = "Token ausente o inválido"),
            @ApiResponse(responseCode = "404", description = "No existe un movimiento con ese ID")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID del movimiento a eliminar", example = "1") @PathVariable Long id) {
        if (service.obtenerPorId(id) == null) {
            return ResponseEntity.notFound().build();
        }
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}