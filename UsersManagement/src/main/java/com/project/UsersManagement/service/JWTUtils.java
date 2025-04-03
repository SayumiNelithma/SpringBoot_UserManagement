package com.project.UsersManagement.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtParser;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
// import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JWTUtils {

    private SecretKey key;
    private static final long EXPIRATION_TIME = 86400000;  // 24 hours

    // Constructor where you initialize the secret key for signing JWT tokens
    public JWTUtils() {
        String secretString = "843567893696976453275974432697R634976R738467TR678T34865R6834R8763T478378637664538745673865783678548735687R3";
        byte[] keyBytes = Base64.getDecoder().decode(secretString.getBytes(StandardCharsets.UTF_8));
        this.key = new SecretKeySpec(keyBytes, "HmacSHA256");
    }

    // Generate access token (JWT)
    public String generateToken(UserDetails userDetails) {
        JwtBuilder jwtBuilder = Jwts.builder()
                .setSubject(userDetails.getUsername())  // Set the subject as the username
                .setIssuedAt(new Date(System.currentTimeMillis()))  // Set issue time
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))  // Set expiration time
                .signWith(key);  // Sign the JWT with the secret key

        return jwtBuilder.compact();  // Generate the compact JWT token
    }

    // Generate refresh token with custom claims
    public String generateRefreshToken(Map<String, Object> claims, UserDetails userDetails) {
        JwtBuilder jwtBuilder = Jwts.builder()
                .setClaims(claims)  // Set custom claims
                .setSubject(userDetails.getUsername())  // Set the subject as the username
                .setIssuedAt(new Date(System.currentTimeMillis()))  // Set issue time
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))  // Set expiration time
                .signWith(key);  // Sign the JWT with the secret key

        return jwtBuilder.compact();  // Generate the compact JWT token
    }

    // Extract username from the JWT token
    public String extractUsername(String token) {
        return extractClaims(token, Claims::getSubject);  // Extract username (subject) from token
    }

    // Extract any claims from the JWT token
    private <T> T extractClaims(String token, Function<Claims, T> claimsFunction) {
        JwtParser jwtParser = Jwts.parserBuilder()  // Using parserBuilder instead of parser
                .setSigningKey(key)                  // Set the signing key
                .build();                            // Build the parser
        Claims claims = jwtParser.parseClaimsJws(token).getBody();  // Parse the claims from the token
        return claimsFunction.apply(claims);  // Apply the claims function to get specific claims
    }

    // Check if the token is valid for the given user details
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);  // Extract username from the token
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));  // Validate the token
    }

    // Check if the token has expired
    public boolean isTokenExpired(String token) {
        return extractClaims(token, Claims::getExpiration).before(new Date());  // Check if the token's expiration date is before the current time
    }
}
