package com.example.logitrack.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 1. Obtener el header "Authorization" de la petición
        String authHeader = request.getHeader("Authorization");

        // 2. Verificar si existe y empieza con "Bearer "
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // Quitamos "Bearer " para quedarnos solo con el token

            try {
                // 3. Extraer la información del token
                String username = jwtUtils.extractUsername(token);
                String role = jwtUtils.extractRole(token);

                // 4. Si el token es válido y no hay una autenticación previa, la creamos
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                    // Spring Security espera que los roles empiecen con "ROLE_"
                    SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role);

                    // Creamos el objeto de autenticación (usuario, contraseña null porque ya validamos el token, y sus roles)
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(username, null, Collections.singletonList(authority));

                    // Se lo entregamos a Spring para que sepa que este usuario está autenticado
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            } catch (Exception e) {
                // Si el token está vencido o manipulado, simplemente no autenticamos.
                // Spring se encargará de devolver un error 401 más adelante.
                System.out.println("Token inválido o expirado: " + e.getMessage());
            }
        }

        // 5. Continuar con el resto de filtros de Spring Security
        filterChain.doFilter(request, response);
    }
}