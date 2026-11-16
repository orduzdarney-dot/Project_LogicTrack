package com.example.logitrack.modelo;


import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.math.BigDecimal;


@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class Producto {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100, unique = true)
    private String nombre;

    @Column(nullable = false, length = 100)
    private String categoria;

    @Column(nullable = false)
    @Min(value = 0, message = "el stock no puede ser negativo")
    private Integer stock;

    @Column(nullable = false)
    @DecimalMin(value = "0.0", message = "el precio no puede ser negativo")
    private BigDecimal precio;

}
