package com.example.logitrack.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtils {

    // Clave secreta para firmar los tokens (en producción va en application.properties)
    private final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(
            "EstaEsUnaClaveSecretaMuyLargaYSeguraParaFirmarLosTokensJWT123456789".getBytes(StandardCharsets.UTF_8)
    );

    // Tiempo de expiración del token (24 horas en milisegundos)
    private final long EXPIRATION_TIME = 86400000;

    //  GENERAR EL TOKEN (La pulsera)
    public String generateToken(String username, String role) {
        return Jwts.builder()
                .subject(username)
                .claim("role", role) // Guardamos el rol dentro del token
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET_KEY)
                .compact();
    }

    // 2. LEER EL TOKEN (Verificar la pulsera)
    public Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(SECRET_KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    //  OBTENER EL USUARIO DEL TOKEN
    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    //  OBTENER EL ROL DEL TOKEN
    public String extractRole(String token) {
        return extractClaims(token).get("role", String.class);
    }

    //  VALIDAR SI EL TOKEN ES VÁLIDO
    public boolean isTokenValid(String token, String username) {
        return (username.equals(extractUsername(token)) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }
}