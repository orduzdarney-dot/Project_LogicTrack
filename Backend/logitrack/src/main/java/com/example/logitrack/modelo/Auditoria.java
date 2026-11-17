package com.example.logitrack.modelo;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Auditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "El tipo de operación es obligatorio")
    @Column(nullable = false)
    private TipoOperacion tipoOperacion;

    @NotNull(message = "La fecha es obligatoria")
    @Column(nullable = false)
    private LocalDateTime fecha;

    @NotNull(message = "La entidad afectada es obligatoria")
    @Column(nullable = false, length = 50)
    private String entidadAfectada;

    @NotNull(message = "El ID del registro es obligatorio")
    @Column(nullable = false)
    private Long idRegistroAfectado;

    @Column(columnDefinition = "TEXT")
    private String valoresAnteriores;

    @Column(columnDefinition = "TEXT")
    private String valoresNuevos;

    // ✅ CAMBIO: Guardamos el nombre como texto, no como relación.
    // Esto preserva la integridad histórica aunque el usuario se borre después.
    @Column(name = "nombre_usuario", length = 50)
    private String nombreUsuario;
}