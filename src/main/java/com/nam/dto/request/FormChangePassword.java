package com.nam.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FormChangePassword {
	private String currentPassword;
	private String newPassword1;
	private String newPassword2;
}
