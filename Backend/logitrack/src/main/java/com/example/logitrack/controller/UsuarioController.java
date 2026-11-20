package com.example.logitrack.controller;

import com.example.logitrack.modelo.Usuario;
import com.example.logitrack.service.UsuarioService;
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
@RequestMapping("/usuarios")
@Tag(name = "Usuarios", description = "Gestión de usuarios del sistema y sus roles (ADMIN / EMPLEADO)")
public class UsuarioController {

    @Autowired
    private UsuarioService service;

    @Operation(summary = "Listar todos los usuarios",
            description = "Devuelve todos los usuarios registrados en el sistema.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente"),
            @ApiResponse(responseCode = "401", description = "Token ausente o inválido")
    })
    @GetMapping
    public ResponseEntity<List<Usuario>> obtenerTodos() {
        return ResponseEntity.ok(service.obtenerTodos());
    }

    @Operation(summary = "Consultar un usuario por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "401", description = "Token ausente o inválido"),
            @ApiResponse(responseCode = "404", description = "No existe un usuario con ese ID")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> obtenerPorId(
            @Parameter(description = "ID del usuario", example = "1") @PathVariable Long id) {
        Usuario usuario = service.obtenerPorId(id);
        if (usuario == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(usuario);
    }

    @Operation(summary = "Crear un usuario",
            description = "Registra un nuevo usuario. El correo debe ser único y la contraseña "
                    + "(entre 5 y 100 caracteres) se almacena encriptada con BCrypt. "
                    + "Roles disponibles: ADMIN, EMPLEADO.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = @ExampleObject(name = "Usuario", value = """
                    {
                      "nombre": "Carlos Ruiz",
                      "correo": "carlos@logitrack.com",
                      "password": "clave123",
                      "rol": "EMPLEADO"
                    }
                    """)))
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuario creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o correo ya registrado"),
            @ApiResponse(responseCode = "401", description = "Token ausente o inválido")
    })
    @PostMapping
    public ResponseEntity<Usuario> crear(@RequestBody Usuario usuario) {
        Usuario nuevo = service.guardar(usuario);
        return ResponseEntity.status(201).body(nuevo);
    }

    @Operation(summary = "Actualizar un usuario",
            description = "Reemplaza los datos del usuario indicado. La contraseña enviada se encripta antes de guardarse.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = @ExampleObject(name = "Usuario", value = """
                    {
                      "nombre": "Carlos Ruiz",
                      "correo": "carlos.ruiz@logitrack.com",
                      "password": "nuevaClave456",
                      "rol": "ADMIN"
                    }
                    """)))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario actualizado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o correo ya registrado"),
            @ApiResponse(responseCode = "401", description = "Token ausente o inválido"),
            @ApiResponse(responseCode = "404", description = "No existe un usuario con ese ID")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Usuario> actualizar(
            @Parameter(description = "ID del usuario a actualizar", example = "1") @PathVariable Long id,
            @RequestBody Usuario usuario) {
        Usuario existente = service.obtenerPorId(id);
        if (existente == null) {
            return ResponseEntity.notFound().build();
        }
        usuario.setId(id);
        return ResponseEntity.ok(service.guardar(usuario));
    }

    @Operation(summary = "Eliminar un usuario", description = "Solo para usuarios con rol ADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Usuario eliminado"),
            @ApiResponse(responseCode = "401", description = "Token ausente o inválido"),
            @ApiResponse(responseCode = "403", description = "Sin permisos: se requiere rol ADMIN"),
            @ApiResponse(responseCode = "404", description = "No existe un usuario con ese ID")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID del usuario a eliminar", example = "1") @PathVariable Long id) {
        if (service.obtenerPorId(id) == null) {
            return ResponseEntity.notFound().build();
        }
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}