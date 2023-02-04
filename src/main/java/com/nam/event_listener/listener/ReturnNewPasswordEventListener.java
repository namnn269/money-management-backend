package com.nam.event_listener.listener;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.nam.dto.server.EmailDto;
import com.nam.entity.User;
import com.nam.event_listener.ReturnNewPasswordEvent;
import com.nam.repository.IUserRepository;
import com.nam.service.IEmailService;

@Component
public class ReturnNewPasswordEventListener implements ApplicationListener<ReturnNewPasswordEvent> {

	@Autowired
	private IEmailService emailService;
	@Autowired
	private IUserRepository userRepo;
	@Autowired
	private PasswordEncoder encoder;
	
	@Override
	public void onApplicationEvent(ReturnNewPasswordEvent event) {
		User user = event.getUser();
		String newPassword = UUID.randomUUID().toString().substring(0, 8);
		user.setPassword(encoder.encode(newPassword));
		userRepo.save(user);
		String content =  "<h2>Provide new password for account: " + user.getUsername() + "</h2>"
						+ "<p>Your new password is: " + newPassword + "</p>";
		
		EmailDto emailDto = EmailDto.builder()
				.subject("Provide new password")
				.to(user.getEmail())
				.content(content)
				.build();
		emailService.sendEmail(emailDto);
	}

}
