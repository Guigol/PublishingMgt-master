package com.publ.PublishingMgt_master.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtProvider {

    // Fixed secret key read from application.properties
    @Value("${jwt.secret}")
    private String jwtSecret;

    private Key key;

    @PostConstruct
    public void init() {
        // Creates an HMAC key from the fixed character string
        key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    // -----------------------------
    // Token verification
    // -----------------------------
    public boolean validateToken(String jwt) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(jwt);
            return true;
        } catch (JwtException e) {
           // System.out.println("❌ JWT invalid: " + e.getMessage());
            return false;
        }
    }

    public Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // -----------------------------
    // Token generation
    // -----------------------------
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername());
    }

    public String generateToken(Authentication authentication) {
        User principal = (User) authentication.getPrincipal();
        List<String> roles = principal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", roles);

        return createToken(claims, principal.getUsername(), 1000 * 60 * 60); // 1h
    }

    public String generateToken(String username, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", role);
        return createToken(claims, username, 1000 * 60 * 60); // 1h
    }

    public String generateTokenWithExpiry(String username, String role, long millis) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", role);
        return createToken(claims, username, millis);
    }

    private String createToken(Map<String, Object> claims, String subject, long validityMillis) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + validityMillis))
                .signWith(key)
                .compact();
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return createToken(claims, subject, 1000 * 60 * 60); // 1h par défaut
    }

    public String generateAccessToken(String username, String role) {
        return generateToken(username, role);
    }

    // -----------------------------
    // JWT info extraction
    // -----------------------------
    public String getUsernameFromJwt(String token) {
        return extractAllClaims(token).getSubject();
    }

    public List<String> getRolesFromToken(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("roles", List.class);
    }
}