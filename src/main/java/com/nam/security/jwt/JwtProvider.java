package com.nam.security.jwt;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.nam.entity.RefreshToken;
import com.nam.entity.User;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

@Component
public class JwtProvider {

	public static final Logger logger = LoggerFactory.getLogger(JwtProvider.class);

	private final String SECRET_ACCESS_TOKEN_KEY = "access_secret";
	private final String SECRET_REFRESH_TOKEN_KEY = "refresh_secret";
	private final long EXPIRATION_TIME_REFRESH_TOKEN = 3 * 30 * 24 * 60 * 60; // seconds
	private final long EXPIRATION_TIME_ACCESS_TOKEN = 60 * 60;  // seconds

	public String generateAccessToken(User user) {

		return Jwts.builder()
				.setSubject(user.getUsername())
				.signWith(SignatureAlgorithm.HS256, SECRET_ACCESS_TOKEN_KEY)
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME_ACCESS_TOKEN * 1000)).compact();
	}

	public String generateRefreshToken(User user, RefreshToken refreshToken) {

		return Jwts.builder()
				.setSubject(user.getUsername())
				.claim("refreshTokenId", refreshToken.getId())
				.signWith(SignatureAlgorithm.HS256, SECRET_REFRESH_TOKEN_KEY)
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME_REFRESH_TOKEN * 1000)).compact();
	}

	
	public boolean validateToken(String token, String secretKey) {
		try {
			Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
			return true;
		} catch (ExpiredJwtException e) {
			logger.error("Expired Jwt Exception -> message: {}", e.getMessage());
		} catch (IllegalArgumentException e) {
			logger.error("Jwt claims token is empty -> message: {}", e.getMessage());
		} catch (SignatureException e) {
			logger.error("Signature Token Exception -> message: {}", e.getMessage());
		} catch (MalformedJwtException e) {
			logger.error("Malformed token -> message: {}", e.getMessage());
		} catch (UnsupportedJwtException e) {
			logger.error("Unsupported token -> message: {}", e.getMessage());
		}
		return false;
	}
	
	public boolean validateAccessToken(String accessToken) {
		return validateToken(accessToken, SECRET_ACCESS_TOKEN_KEY);
	}
	
	public boolean validateRefreshToken(String refreshToken) {
		return validateToken(refreshToken, SECRET_REFRESH_TOKEN_KEY); 
	}
	
	public String getUsernameFromAccessToken(String accessToken) {
		return Jwts.parser().setSigningKey(SECRET_ACCESS_TOKEN_KEY).parseClaimsJws(accessToken).getBody().getSubject();
	}

	public String getUsernameFromRefreshToken(String refreshToken) {
		return Jwts.parser().setSigningKey(SECRET_REFRESH_TOKEN_KEY).parseClaimsJws(refreshToken).getBody().getSubject();
	}
	
	public Long getRefreshTokenIdFromRefreshToken(String refreshToken) {
		Integer id = (Integer)Jwts.parser()
				.setSigningKey(SECRET_REFRESH_TOKEN_KEY)
				.parseClaimsJws(refreshToken)
				.getBody()
				.get("refreshTokenId");
		return id.longValue();
	}
}











