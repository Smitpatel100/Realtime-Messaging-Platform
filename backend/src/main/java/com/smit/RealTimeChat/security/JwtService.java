package com.smit.RealTimeChat.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
public class JwtService {

    private final String jwtSecret;
    private final long jwtExpiration;

    public JwtService(
            @Value("${jwt.secret}") String jwtSecret,
            @Value("${jwt.expiration}") long jwtExpiration
    ) {
        this.jwtSecret = jwtSecret;
        this.jwtExpiration = jwtExpiration;
    }

    // ── UNCHANGED ────────────────────────────────────────────────────────
    public String generateToken(String email) {

        Key signingKey = Keys.hmacShaKeyFor(jwtSecret.getBytes());

        Date issuedAt = new Date();
        Date expiresAt = new Date(issuedAt.getTime() + jwtExpiration);

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(issuedAt)
                .setExpiration(expiresAt)
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    // ── NEW METHODS ──────────────────────────────────────────────────────

    public String extractEmail(String token) {
        return extractAllClaims(token).getSubject();
    }

    public boolean isTokenValid(String token, String email) {
        String extractedEmail = extractEmail(token);
        return extractedEmail.equals(email) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        Date expiration = extractAllClaims(token).getExpiration();
        return expiration.before(new Date());
    }

    private Claims extractAllClaims(String token) {
        Key signingKey = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}