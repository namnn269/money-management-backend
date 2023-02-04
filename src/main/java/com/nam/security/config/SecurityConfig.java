package com.nam.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.nam.entity.RoleName;
import com.nam.security.exception_handling.CustomAccessDeniedHandler;
import com.nam.security.exception_handling.CustomAuthenticationEntryPoint;
import com.nam.security.jwt.JwtFilter;
import com.nam.security.principal.UserDetailServiceImpl;

@EnableWebSecurity
@Configuration
public class SecurityConfig {
	
	@Autowired
	private UserDetailServiceImpl userDetailServiceImpl;
	@Autowired
	private CustomAccessDeniedHandler accessDeniedHandler;
	@Autowired
	private CustomAuthenticationEntryPoint authenticationEntryPoint;
	
	@Bean
	public JwtFilter jwtFilter() {
		return new JwtFilter();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
		return configuration.getAuthenticationManager();
	}
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		http.cors().and().csrf().disable()
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.authorizeHttpRequests(auth -> auth
				.antMatchers("/api/v1/auth/**").permitAll()
				.antMatchers("/api/v1/admin/**").hasAuthority(RoleName.ADMIN.name())
				.anyRequest().authenticated())
			.exceptionHandling(exc->exc
				.authenticationEntryPoint(authenticationEntryPoint)
				.accessDeniedHandler(accessDeniedHandler))
			.userDetailsService(userDetailServiceImpl)
			.addFilterBefore(jwtFilter(), UsernamePasswordAuthenticationFilter.class);
		return http.build();
	}

}











