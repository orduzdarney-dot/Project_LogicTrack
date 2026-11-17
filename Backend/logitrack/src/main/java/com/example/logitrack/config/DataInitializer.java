package com.example.logitrack.config;

import com.example.logitrack.modelo.Rol;
import com.example.logitrack.modelo.Usuario;
import com.example.logitrack.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Solo crea el admin si NO existe ya (evita duplicados al reiniciar)
        if (!usuarioRepository.existsByCorreo("admin@logitrack.com")) {
            Usuario admin = new Usuario();
            admin.setNombre("admin1");
            admin.setCorreo("admin@logitrack.com");
            admin.setPassword(passwordEncoder.encode("12345")); // ← GENERA EL HASH CORRECTO
            admin.setRol(Rol.ADMIN);
            usuarioRepository.save(admin);
            System.out.println("✅ Usuario ADMIN inicial creado con contraseña encriptada.");
        } else {
            System.out.println("ℹ️ El usuario admin ya existe, no se crea de nuevo.");
        }
    }
}