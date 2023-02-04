package com.nam.event_listener.listener;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.nam.dto.server.EmailDto;
import com.nam.entity.ResetPasswordToken;
import com.nam.entity.User;
import com.nam.event_listener.ResetPasswordEvent;
import com.nam.repository.IResetPasswordTokenRepository;
import com.nam.service.IEmailService;

@Component
public class ResetPasswordEventListener implements ApplicationListener<ResetPasswordEvent> {

	@Autowired
	private IEmailService emailService;
	@Autowired
	private IResetPasswordTokenRepository resetPasswordTokenRepo;
	
	@Override
	public void onApplicationEvent(ResetPasswordEvent event) {
		
		User user = event.getUser();
		ResetPasswordToken resetPasswordToken;
		Optional<ResetPasswordToken> opToken = resetPasswordTokenRepo.findByUser(user);
		if (opToken.isEmpty()) {
			resetPasswordToken = new ResetPasswordToken(user, 60);
		} else {
			resetPasswordToken = opToken.get();
			resetPasswordToken.resetToken();
		}
		resetPasswordTokenRepo.save(resetPasswordToken);
		String url = event.getUrl() + "/api/v1/auth/receive-reset-password-token-from-email?token=" + resetPasswordToken.getToken();

		String content =  "<h2>Click the following link to reset your password for account: " + user.getUsername() + "</h2>"
				+ "<a href = '" + url + "'>Reset password</a>"
				+ "<p>The link will expire after " + resetPasswordToken.getEXPIRATION()/60 + " minutes</p>";
		
		EmailDto emailDto = EmailDto.builder()
				.subject("Reset password for user: " + user.getUsername())
				.to(user.getEmail())
				.content(content)
				.build();
		
		emailService.sendEmail(emailDto);
	}

}
