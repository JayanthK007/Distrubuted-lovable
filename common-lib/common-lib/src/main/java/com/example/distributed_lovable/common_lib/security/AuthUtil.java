package com.example.distributed_lovable.common_lib.security;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;

import javax.crypto.SecretKey;

import com.example.distributed_lovable.common_lib.dto.UserDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;


@Component
public class AuthUtil {
	
	@Value("${jwt.secretKey}")
	private String jwtSecretKey;
	
	private SecretKey getSecretKey() {
		return Keys.hmacShaKeyFor(jwtSecretKey.getBytes(StandardCharsets.UTF_8));
	}
	
	public String generateAccessToken(JwtUserPrinciple user) {
		return Jwts.builder()
				.subject(user.username())
				.claim("userId", user.userId())
				.claim("name", user.name())
				.issuedAt(new Date())
				.expiration(new Date(System.currentTimeMillis() + 10 * 60 * 1000))
				.signWith(getSecretKey(), Jwts.SIG.HS256)
				.compact();
	}
	
	public JwtUserPrinciple verifyToken(String token) {
		Claims claims = Jwts.parser()
						.verifyWith(getSecretKey())
						.build()
						.parseSignedClaims(token)
						.getPayload();
		
		Long userId = ((Number) claims.get("userId")).longValue();		
		String username = claims.getSubject();
		String name = claims.get("name", String.class);
		return new JwtUserPrinciple(userId, name, username,null, new ArrayList<>());
	}
	
	
	public Long getCurrentUserId() {
	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

	    if (authentication == null || !(authentication.getPrincipal() instanceof JwtUserPrinciple)) {
	        throw new AuthenticationCredentialsNotFoundException("No JWT Found");
	    }

	    JwtUserPrinciple userPrincipal = (JwtUserPrinciple) authentication.getPrincipal();

	    return userPrincipal.userId();
	}
}
