package com.example.logitrack.controller;

import com.example.logitrack.dto.LoginRequest;
import com.example.logitrack.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticación", description = "Inicio de sesión y obtención del token JWT")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Operation(summary = "Iniciar sesión",
            description = "Valida el correo y la contraseña del usuario y devuelve un token JWT. "
                    + "Cópialo y pégalo en el botón Authorize para consumir el resto de endpoints. "
                    + "Este endpoint es público: no requiere token.")
    @SecurityRequirements // Endpoint público: quita el candado Bearer global
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Credenciales del usuario",
            content = @Content(examples = @ExampleObject(name = "Login", value = """
                    {
                      "correo": "admin@logitrack.com",
                      "password": "admin123"
                    }
                    """)))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login exitoso, devuelve el token JWT",
                    content = @Content(examples = @ExampleObject(value = """
                            {
                              "token": "eyJhbGciOiJIUzI1NiJ9..."
                            }
                            """))),
            @ApiResponse(responseCode = "401", description = "Usuario no encontrado o contraseña incorrecta",
                    content = @Content(examples = @ExampleObject(value = """
                            {
                              "error": "Contraseña incorrecta"
                            }
                            """)))
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            String token = authService.login(request);
            // Devolvemos el token en un JSON bonito
            return ResponseEntity.ok(Map.of("token", token));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("error", e.getMessage()));
        }
    }
}