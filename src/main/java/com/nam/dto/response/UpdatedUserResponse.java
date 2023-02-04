package com.nam.dto.response;

import java.util.Collection;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdatedUserResponse {
	private Long id;
	private String username;
	private String avatar;
	private String email;
	private boolean verifiedEmail;
	private Collection<String> roles;
}
