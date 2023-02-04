package com.nam.security.jwt;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.nam.security.principal.UserDetailServiceImpl;

public class JwtFilter extends OncePerRequestFilter {
	static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);

	@Autowired
	private JwtProvider jwtProvider;
	@Autowired
	private UserDetailServiceImpl userDetailServiceImpl;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		try {
			String token = getToken(request);
			if (StringUtils.hasText(token) && token != null) {
				String username = jwtProvider.getUsernameFromAccessToken(token);
				UserDetails userDetail = userDetailServiceImpl.loadUserByUsername(username);
				UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
						username, null, userDetail.getAuthorities());
				authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(authenticationToken);
			}
		} catch (Exception e) {
			logger.error("Can not set user authentication -> message: {}", e.getMessage());
		}

		filterChain.doFilter(request, response);
	}

	public String getToken(HttpServletRequest request) {
		String headerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
		if (StringUtils.hasText(headerToken) && headerToken.trim().startsWith("Bearer ")) {
			return headerToken.trim().replace("Bearer ", "");
		}
		logger.error("Token is invalid!");
		return null;
	}

}
