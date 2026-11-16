package com.example.logitrack.modelo;


import jakarta.persistence.*;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,length = 50, unique = true)
    @Size(min = 3, max = 50, message = "El nombre debe tener entre 3 y 50 caracteres")
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @Column(nullable = false,length = 100, unique = true)
    @Email(message = "el correo debe tener un formato valido")
    @NotBlank (message = "el correo es obligatorio")
    private String correo;


    @Column(nullable = false,length = 50)
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 5, max = 20, message = "la contraseña debe contener entre 5 y 20 caracteres")
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rol rol;

}
