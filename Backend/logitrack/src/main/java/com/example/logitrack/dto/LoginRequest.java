package com.example.logitrack.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Credenciales para iniciar sesión")
public class LoginRequest {

    @Schema(description = "Correo electrónico del usuario", example = "admin@logitrack.com")
    private String correo;

    @Schema(description = "Contraseña del usuario", example = "admin123")
    private String password;
}