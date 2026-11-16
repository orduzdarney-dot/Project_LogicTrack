package com.example.logitrack.modelo;

import jakarta.persistence.*;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Bodega {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false, length = 100)
    private String nombre;


    @Column(nullable = false, length = 200)
    private String ubicacion;


    @Column(nullable = false)
    private Integer capacidad;


    private String encargado;



}
