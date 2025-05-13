package com.emp_mgmt_sys.utils;

import com.emp_mgmt_sys.enums.UserRole;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtUtil {

    private SecretKey key;

    @PostConstruct
    public void init() {
        // 32+ chars for HS256
        String SECRET = "4fcb7b8e3a5379486231ac9a5bb26fd0b3172c6c2e65d57278d098d80c928b65";
        this.key = Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    // Generate JWT token
    public String generateToken(String email) {
        long EXPIRATION = 1000 * 60 * 60 * 10L; // 10 hours
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // Extract email (used as username)
    public String extractEmail(String token) {
        return extractAllClaims(token).getSubject();
    }

    // Extract role from token
    public UserRole extractRole(String token) {
        String roleStr = extractAllClaims(token).get("role", String.class);
        return UserRole.valueOf(roleStr);
    }

    // Validate token (signature + expiration)
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return !isTokenExpired(token);
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // Helpers
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }
}
