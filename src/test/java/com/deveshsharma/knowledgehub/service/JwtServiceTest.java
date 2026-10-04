package com.deveshsharma.knowledgehub.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for JWT creation, claim extraction, and type validation.
 */
class JwtServiceTest {

    private JwtService jwtService;
    private UserDetails user;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey", "e7c7LrR11XwFnzPcuA0B4su450F4BU6nBJ2uDl4xRmE");
        ReflectionTestUtils.setField(jwtService, "accessExpirationTime", 60_000L);
        ReflectionTestUtils.setField(jwtService, "refreshExpirationTime", 120_000L);
        user = User.withUsername("jwt@example.com").password("ignored").roles("USER").build();
    }

    /**
     * Verifies access and refresh tokens contain their expected subject and type.
     */
    @Test
    void generatedTokensContainExpectedClaims() {
        String accessToken = jwtService.generateAccessToken(new HashMap<>(), user);
        String refreshToken = jwtService.generateRefreshToken(new HashMap<>(), user);

        assertEquals("jwt@example.com", jwtService.extractUsername(accessToken));
        assertTrue(jwtService.isTokenValid(accessToken, user, "access_token"));
        assertTrue(jwtService.isTokenValid(refreshToken, user, "refresh_token"));
    }

    /**
     * Verifies a token is rejected when the expected token type does not match.
     */
    @Test
    void tokenTypeMustMatch() {
        Map<String, Object> claims = new HashMap<>();
        String token = jwtService.generateAccessToken(claims, user);

        assertTrue(!jwtService.isTokenValid(token, user, "refresh_token"));
    }
}
