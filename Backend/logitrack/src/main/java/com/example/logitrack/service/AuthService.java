package com.example.logitrack.service;

import com.example.logitrack.dto.LoginRequest;
import com.example.logitrack.modelo.Usuario;
import com.example.logitrack.repository.UsuarioRepository;
import com.example.logitrack.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder; // La máquina de encriptar

    public String login(LoginRequest request) {
        // 1. Buscar al usuario por su correo
        Usuario usuario = usuarioRepository.findByCorreo(request.getCorreo())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 2. Verificar si la contraseña coincide con la encriptada
        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        // 3. Si todo está bien, generar la "pulsera" (Token JWT)
        // Pasamos el nombre y el rol (convertido a String)
        return jwtUtils.generateToken(usuario.getNombre(), usuario.getRol().name());
    }
}