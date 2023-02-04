package com.nam.event_listener.listener;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.nam.dto.server.EmailDto;
import com.nam.entity.User;
import com.nam.entity.VerifyEmailToken;
import com.nam.event_listener.VerifyEmailEvent;
import com.nam.repository.IVerifyEmailTokenRepository;
import com.nam.service.IEmailService;

@Component
public class VerifyEmailEventListener implements ApplicationListener<VerifyEmailEvent> {

	@Autowired
	private IEmailService emailService;
	@Autowired
	private IVerifyEmailTokenRepository verifyEmailTokenRepo;
	
	@Override
	public void onApplicationEvent(VerifyEmailEvent event) {
		User user = event.getUser();
		if(user.isVerifiedEmail())
			return;
		String url = event.getUrl();
		Optional<VerifyEmailToken> verifyEmailTokenOp = verifyEmailTokenRepo.findByUser(user);
		VerifyEmailToken verifyEmailToken;
		if (verifyEmailTokenOp.isEmpty()) {
			verifyEmailToken = new VerifyEmailToken(user, 60);
		} else {
			verifyEmailToken = verifyEmailTokenOp.get();
			verifyEmailToken.resetToken();
		}
		verifyEmailTokenRepo.save(verifyEmailToken);

		String verifiedUrl = url + "/api/v1/auth/receive-token-from-email?token=" + verifyEmailToken.getToken();

		String content =  "<h2>Click the following link to verify your email</h2>"
						+ "<a href = '" + verifiedUrl + "'>Verify email</a>"
						+ "<p>The link will expire after " + verifyEmailToken.getEXPIRATION()/60 + " minutes</p>";
		
		EmailDto emailDto = EmailDto.builder()
				.subject("Confirm email for money management app")
				.to(user.getEmail())
				.content(content)
				.build();
		
		emailService.sendEmail(emailDto);
	}

}
