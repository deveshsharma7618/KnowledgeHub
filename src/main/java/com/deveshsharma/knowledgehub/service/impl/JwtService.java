package com.deveshsharma.knowledgehub.service.impl;


import com.deveshsharma.knowledgehub.constants.JwtConstants;
import com.deveshsharma.knowledgehub.service.JwtTokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * Creates and validates signed access and refresh JWTs.
 */
@Service
public class JwtService implements JwtTokenService {
    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access-expiration-time}")
    private long accessExpirationTime;

    @Value("${jwt.refresh-expiration-time}")
    private long refreshExpirationTime;

    /**
     * Extracts the subject email from a token.
     *
     * @param token signed JWT
     * @return token subject
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Resolves an arbitrary claim from a signed token.
     *
     * @param token signed JWT
     * @param claimsResolver claim mapping function
     * @param <T> resolved claim type
     * @return resolved claim
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }


    /**
     * Generates a token marked as an access token.
     *
     * @param extraClaims additional JWT claims
     * @param userDetails token subject details
     * @return signed access JWT
     */
    public String generateAccessToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        extraClaims.put(JwtConstants.TOKEN_TYPE, JwtConstants.ACCESS_TOKEN);
        return buildToken(extraClaims, userDetails, accessExpirationTime);
    }

    /**
     * Generates a token marked as a refresh token.
     *
     * @param extraClaims additional JWT claims
     * @param userDetails token subject details
     * @return signed refresh JWT
     */
    public String generateRefreshToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        extraClaims.put(JwtConstants.TOKEN_TYPE, JwtConstants.REFRESH_TOKEN);
        return buildToken(extraClaims, userDetails, refreshExpirationTime);
    }

    /**
     * Builds and signs a JWT with the supplied claims and lifetime.
     *
     * @param extraClaims additional JWT claims
     * @param userDetails token subject details
     * @param expiration token lifetime in milliseconds
     * @return signed compact JWT
     */
    private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails, long expiration) {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .compact();
    }

    /**
     * Validates the subject, expiration, and token type.
     *
     * @param token signed JWT
     * @param userDetails expected subject details
     * @param type expected token type
     * @return whether the token is valid for the supplied user and type
     */
    public boolean isTokenValid(String token, UserDetails userDetails, String type) {
        final String username = extractUsername(token);
        final Claims claims = extractAllClaims(token);
        return username.equals(userDetails.getUsername())
                && !isTokenExpired(token)
                && Objects.equals(claims.get(JwtConstants.TOKEN_TYPE, String.class), type);
    }


    /**
     * Checks whether a token's expiration date is before the current time.
     *
     * @param token signed JWT
     * @return whether the token has expired
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).isBefore(Instant.now());
    }

    /**
     * Extracts the expiration claim from a token.
     *
     * @param token signed JWT
     * @return token expiration timestamp
     */
    public Instant extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration).toInstant();
    }

    /**
     * Parses and verifies a signed token.
     *
     * @param token signed JWT
     * @return verified token claims
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith( Keys.hmacShaKeyFor(secretKey.getBytes()) )
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

}
