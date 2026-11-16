package com.example.logitrack.modelo;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
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
public class Movimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "la fecha es obligatoria")
    @Column(nullable = false)
    private LocalDateTime fecha;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "el tipo de movimiento es obligatorio")
    @Column(nullable = false)
    private TipoMovimiento tipo;

    @NotNull(message = "la cantidad es obligatoria")
    @Min(value = 1, message = "la cantidad debe ser al menos 1")
    @Column(nullable = false)
    private Integer cantidad;

    // Relación con Usuario (quién registró el movimiento)
    @NotNull(message = "el usuario es obligatorio")
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    // Relación con Producto (qué producto se movió)
    @NotNull(message = "el producto es obligatorio")
    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    // Relación con Bodega origen (de dónde sale)
    // OPCIONAL: puede ser null si es una ENTRADA
    @ManyToOne
    @JoinColumn(name = "bodega_origen_id")
    private Bodega bodegaOrigen;

    // Relación con Bodega destino (a dónde llega)
    // OPCIONAL: puede ser null si es una SALIDA
    @ManyToOne
    @JoinColumn(name = "bodega_destino_id")
    private Bodega bodegaDestino;
}