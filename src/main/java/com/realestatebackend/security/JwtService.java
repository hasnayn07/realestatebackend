package com.realestatebackend.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {
    private final Key key;
    private final long accessTtlMs;
    private final long refreshTtlMs;

    public JwtService(@Value("${jwt.secret}") String secret,
                      @Value("${jwt.expirationMinutes}") long expMin,
                      @Value("${jwt.refreshExpirationDays}") long refreshDays){
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.accessTtlMs = expMin * 60_000L;
        this.refreshTtlMs = refreshDays * 24L * 60L * 60L * 1000L;
    }

    public String generate(Map<String,Object> claims, String subject, boolean refresh){
        long ttl = refresh ? refreshTtlMs : accessTtlMs; Instant now = Instant.now();
        return Jwts.builder().setClaims(claims).setSubject(subject)
                .setIssuedAt(Date.from(now)).setExpiration(Date.from(now.plusMillis(ttl)))
                .signWith(key, SignatureAlgorithm.HS256).compact();
    }

    public Jws<Claims> parse(String token){
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
    }
}
