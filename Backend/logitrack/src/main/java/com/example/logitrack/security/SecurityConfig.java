package com.example.logitrack.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    // ✅ 1. INYECTAR EL SERVICIO PERSONALIZADO DE USUARIOS
    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // ✅ 2. DECIRLE A SPRING QUE USE NUESTRA BASE DE DATOS, NO LA MEMORIA
                .userDetailsService(customUserDetailsService)

                .authorizeHttpRequests(auth -> auth
                        // 1. Rutas públicas
                        .requestMatchers("/auth/**").permitAll()

                        // 1.1 Archivos del frontend (páginas, estilos y scripts)
                        .requestMatchers("/", "/index.html", "/login.html", "/css/**", "/js/**", "/favicon.ico").permitAll()

                        // 2. Swagger
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/swagger-ui.html").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers("/v3/api-docs.yaml").permitAll()
                        .requestMatchers("/v3/api-docs.json").permitAll()

                        // 3. DELETE solo ADMIN
                        .requestMatchers(HttpMethod.DELETE, "/bodegas/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/productos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/usuarios/**").hasRole("ADMIN")

                        // 4. Auditoría solo ADMIN
                        .requestMatchers("/auditorias/**").hasRole("ADMIN")

                        // 5. ADMIN y EMPLEADO
                        .requestMatchers("/bodegas/**").hasAnyRole("ADMIN", "EMPLEADO")
                        .requestMatchers("/productos/**").hasAnyRole("ADMIN", "EMPLEADO")
                        .requestMatchers("/movimientos/**").hasAnyRole("ADMIN", "EMPLEADO")
                        .requestMatchers("/usuarios/**").hasAnyRole("ADMIN", "EMPLEADO")

                        // 6. Resto requiere autenticación
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // Bean de CORS
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    //  Bean de PasswordEncoder
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}