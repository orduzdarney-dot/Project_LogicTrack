package com.example.logitrack.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth

                        // 1. Rutas públicas
                        .requestMatchers("/auth/**").permitAll()

                        // 2. REGLAS ESPECÍFICAS (¡Van PRIMERO!)
                        // Solo el ADMIN puede borrar
                        .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/bodegas/**").hasRole("ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/productos/**").hasRole("ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/usuarios/**").hasRole("ADMIN")

                        // Solo el ADMIN puede ver auditorías
                        .requestMatchers("/auditorias/**").hasRole("ADMIN")

                        // 3. REGLAS GENERALES (¡Van DESPUÉS!)
                        // ADMIN y EMPLEADO pueden leer, crear y actualizar
                        .requestMatchers("/bodegas/**").hasAnyRole("ADMIN", "EMPLEADO")
                        .requestMatchers("/productos/**").hasAnyRole("ADMIN", "EMPLEADO")
                        .requestMatchers("/movimientos/**").hasAnyRole("ADMIN", "EMPLEADO")
                        .requestMatchers("/usuarios/**").hasAnyRole("ADMIN", "EMPLEADO")

                        // 4. Cualquier otra cosa requiere autenticación
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
    // Este método crea la "máquina de encriptar contraseñas"
    @Bean
    public org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder passwordEncoder() {
        return new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
    }
}