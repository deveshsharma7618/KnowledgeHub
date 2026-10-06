package com.deveshsharma.knowledgehub.service;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.Map;
import java.util.function.Function;

public interface JwtTokenService {

    String extractUsername(String token);

    <T> T extractClaim(String token, Function<Claims, T> claimsResolver);

    String generateAccessToken(Map<String, Object> extraClaims, UserDetails userDetails);

    String generateRefreshToken(Map<String, Object> extraClaims, UserDetails userDetails);

    boolean isTokenValid(String token, UserDetails userDetails, String type);

    Instant extractExpiration(String token);
}
