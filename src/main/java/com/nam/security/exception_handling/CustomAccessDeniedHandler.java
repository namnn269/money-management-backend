package com.nam.security.exception_handling;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

	private Logger logger = LoggerFactory.getLogger(CustomAccessDeniedHandler.class);

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
			AccessDeniedException accessDeniedException) throws IOException, ServletException {
		logger.error("Access denied exception -> message: {}", accessDeniedException.getMessage());
		
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		
		Map<String , Object> body= new HashMap<>();
		body.put("Status", HttpServletResponse.SC_FORBIDDEN);
		body.put("Error", "Forbidden");
		body.put("Message", accessDeniedException.getMessage());
		body.put("Path", request.getServletPath());
		
		ObjectMapper mapper=new ObjectMapper();
		mapper.writeValue(response.getOutputStream(), body);
	}
}
