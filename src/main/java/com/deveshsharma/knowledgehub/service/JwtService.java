package com.deveshsharma.knowledgehub.service;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {
    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access-expiration-time}")
    private long accessExpirationTime;

    @Value("${jwt.refresh-expiration-time}")
    private long refreshExpirationTime;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }


    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails, long expirationTime) {
        return buildToken(extraClaims, userDetails, expirationTime);
    }

    public String generateAccessToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        extraClaims.put("type", "access_token");
        return buildToken(extraClaims, userDetails, accessExpirationTime);
    }

    public String generateRefreshToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        extraClaims.put("type", "refresh_token");
        return buildToken(extraClaims, userDetails, refreshExpirationTime);
    }

    private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails, long expiration) {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails, String type) {
        final String username = extractUsername(token);
        final Claims claims = extractAllClaims(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token) && claims.get("type") == type;
    }


    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith( Keys.hmacShaKeyFor(secretKey.getBytes()) )
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

}
