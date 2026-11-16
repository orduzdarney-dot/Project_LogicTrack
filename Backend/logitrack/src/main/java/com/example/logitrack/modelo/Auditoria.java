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

    // Valores anteriores al cambio (en formato JSON o texto)
    @Column(columnDefinition = "TEXT")
    private String valoresAnteriores;

    // Valores nuevos después del cambio (en formato JSON o texto)
    @Column(columnDefinition = "TEXT")
    private String valoresNuevos;

    // Relación con Usuario (quién hizo el cambio)
    @NotNull(message = "El usuario es obligatorio")
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
}