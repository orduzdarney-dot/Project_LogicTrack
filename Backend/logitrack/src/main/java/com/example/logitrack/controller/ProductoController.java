package com.example.logitrack.controller;

import com.example.logitrack.modelo.Producto;
import com.example.logitrack.service.ProductoService;
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
@RequestMapping("/productos")
@Tag(name = "Productos", description = "Gestión del catálogo de productos y consulta de stock")
public class ProductoController {

    @Autowired
    private ProductoService service;

    @Operation(summary = "Listar todos los productos",
            description = "Devuelve el catálogo completo de productos con su stock y precio.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente"),
            @ApiResponse(responseCode = "401", description = "Token ausente o inválido")
    })
    @GetMapping
    public ResponseEntity<List<Producto>> obtenerTodos() {
        return ResponseEntity.ok(service.obtenerTodos());
    }

    @Operation(summary = "Consultar un producto por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Producto encontrado"),
            @ApiResponse(responseCode = "401", description = "Token ausente o inválido"),
            @ApiResponse(responseCode = "404", description = "No existe un producto con ese ID")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Producto> obtenerPorId(
            @Parameter(description = "ID del producto", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }

    @Operation(summary = "Crear un producto",
            description = "Registra un nuevo producto. El nombre debe ser único. La operación queda registrada en la auditoría.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Producto creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos (stock o precio negativo, nombre duplicado)"),
            @ApiResponse(responseCode = "401", description = "Token ausente o inválido")
    })
    @PostMapping
    public ResponseEntity<Producto> crear(@RequestBody Producto producto) {
        Producto nuevo = service.guardar(producto);
        return ResponseEntity.status(201).body(nuevo);
    }

    @Operation(summary = "Actualizar un producto",
            description = "Reemplaza los datos del producto indicado. La operación queda registrada en la auditoría.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Producto actualizado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "Token ausente o inválido"),
            @ApiResponse(responseCode = "404", description = "No existe un producto con ese ID")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizar(
            @Parameter(description = "ID del producto a actualizar", example = "1") @PathVariable Long id,
            @RequestBody Producto producto) {
        service.obtenerPorId(id);
        producto.setId(id);
        return ResponseEntity.ok(service.guardar(producto));
    }

    @Operation(summary = "Eliminar un producto", description = "Solo para usuarios con rol ADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Producto eliminado"),
            @ApiResponse(responseCode = "401", description = "Token ausente o inválido"),
            @ApiResponse(responseCode = "403", description = "Sin permisos: se requiere rol ADMIN"),
            @ApiResponse(responseCode = "404", description = "No existe un producto con ese ID")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID del producto a eliminar", example = "1") @PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Listar productos con stock bajo",
            description = "Devuelve los productos con pocas unidades disponibles, para reponer inventario.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente"),
            @ApiResponse(responseCode = "401", description = "Token ausente o inválido")
    })
    @GetMapping("/stock-bajo")
    public ResponseEntity<List<Producto>> obtenerStockBajo() {
        return ResponseEntity.ok(service.obtenerStockBajo());
    }
}