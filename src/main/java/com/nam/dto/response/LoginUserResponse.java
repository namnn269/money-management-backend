package com.nam.dto.response;

import java.util.Collection;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginUserResponse {
	private Long id;
	private String username;
	private String avatar;
	private String email;
	private boolean verifiedEmail;
	private Collection<String> roles;
	@Default
	private String type = "Bearer";
	private String accessToken;
	private String refreshToken;
}
