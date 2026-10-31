package com.helpdesk.helpdeskpro.security;
import java.util.Date;
import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

	 @Value("${jwt.secret}")
	    private String secret;

	    private SecretKey getSigningKey() {
	    		byte[] keyBytes = Decoders.BASE64.decode(secret.trim().replaceAll("\\s", ""));
	        return Keys.hmacShaKeyFor(keyBytes);
	    }

	    public String generateToken(String username) {

	        long currentTime = System.currentTimeMillis();
	        long expirationTime = currentTime + (1000 * 60 * 60);

	        return Jwts.builder()
	                .subject(username)
	                .issuedAt(new Date(currentTime))
	                .expiration(new Date(expirationTime))
	                .signWith(getSigningKey())
	                .compact();
	    }

	    public String extractUsername(String token) {
	        return extractAllClaims(token).getSubject();
	    }

	    public boolean isTokenValid(String token, String username) {
	        Claims claims = extractAllClaims(token);

	        return claims.getSubject().equals(username)
	                && claims.getExpiration().after(new Date());
	    }

	    private Claims extractAllClaims(String token) {
	        return Jwts.parser()
	                .verifyWith(getSigningKey())
	                .build()
	                .parseSignedClaims(token)
	                .getPayload();
	    }
}
