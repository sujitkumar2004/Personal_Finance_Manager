package com.sujit.personal_Finance_Manager.config;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
    }

    @Test
    void testGenerateTokenAndExtractUsername() {
        String username = "testuser";
        String token = jwtUtil.generateToken(username);

        assertNotNull(token, "Token should not be null");
        String extractedUsername = jwtUtil.extractUsername(token);
        assertEquals(username, extractedUsername, "Extracted username should match original");
    }

    @Test
    void testIsTokenValid_ValidToken() {
        String token = jwtUtil.generateToken("validuser");
        assertTrue(jwtUtil.isTokenValid(token), "Token should be valid");
    }

    @Test
    void testIsTokenValid_InvalidToken() {
        String fakeToken = "invalid.token.value";
        assertFalse(jwtUtil.isTokenValid(fakeToken), "Malformed token should be invalid");
    }

    @Test
    void testIsTokenValid_ExpiredToken() throws InterruptedException {
        // Temporarily create a modified JwtUtil with short expiration
        JwtUtil shortLivedJwtUtil = new JwtUtil() {
            @Override
            public String generateToken(String username) {
                final long shortExpiration = 1000; // 1 second
                return io.jsonwebtoken.Jwts.builder()
                        .setSubject(username)
                        .setIssuedAt(new java.util.Date())
                        .setExpiration(new java.util.Date(System.currentTimeMillis() + shortExpiration))
                        .signWith(io.jsonwebtoken.security.Keys.hmacShaKeyFor(
                                "my-super-secret-key-my-super-secret-key".getBytes()), io.jsonwebtoken.SignatureAlgorithm.HS256)
                        .compact();
            }
        };

        String token = shortLivedJwtUtil.generateToken("expireduser");

        // Wait for token to expire
        Thread.sleep(1500);
        assertFalse(shortLivedJwtUtil.isTokenValid(token), "Expired token should be invalid");
    }
}
