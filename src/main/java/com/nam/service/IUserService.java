package com.nam.service;

import com.nam.dto.request.FormChangePassword;
import com.nam.entity.User;

public interface IUserService {
	boolean emailExisted(String email);

	boolean usernameExisted(String username);

	User save(User user);

	User findById(Long id);

	User findByUsername(String usernameFromRefreshToken);
	
	User getCurrentUser();
	
	boolean changePassword(FormChangePassword changePassword);

	User updateInfo(User user);

	String verifyTokenFromEmail(String token);

	void resendVerifyEmail(String token, String receiveUrl);

	void handleUsernameAndEmailBeforResetPassword(String username, String email, String url);

	void verifyResetPasswordTokenFromEmail(String token);

	void resendEmailForResetPassword(String token, String receiveUrl);
}
