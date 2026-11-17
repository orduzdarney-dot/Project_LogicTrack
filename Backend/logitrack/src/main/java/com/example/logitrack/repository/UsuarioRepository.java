package com.example.logitrack.repository;

import com.example.logitrack.modelo.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.swing.*;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    // Para la validación de duplicados (ESTA ES LA QUE TE FALTA)
    boolean existsByCorreo(String correo);

    // Spring crea este método automáticamente solo con leer su nombre"
    Optional<Usuario> findByCorreo(String correo);

    // Para la auditoría
    java.util.Optional<Usuario> findByNombre(String nombre);




}

