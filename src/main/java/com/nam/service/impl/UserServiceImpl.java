package com.nam.service.impl;

import java.sql.Timestamp;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.nam.dto.request.FormChangePassword;
import com.nam.entity.ResetPasswordToken;
import com.nam.entity.User;
import com.nam.entity.VerifyEmailToken;
import com.nam.event_listener.ResetPasswordEvent;
import com.nam.event_listener.ReturnNewPasswordEvent;
import com.nam.event_listener.VerifyEmailEvent;
import com.nam.exceptions.ExpiredTokenException;
import com.nam.exceptions.ObjectNotFoundExeption;
import com.nam.repository.IResetPasswordTokenRepository;
import com.nam.repository.IUserRepository;
import com.nam.repository.IVerifyEmailTokenRepository;
import com.nam.service.IUserService;

@Service
public class UserServiceImpl implements IUserService {
	
	@Autowired
	private PasswordEncoder encoder;
	@Autowired
	private IUserRepository userRepo;
	@Autowired
	private IVerifyEmailTokenRepository verifyEmailTokenRepo;
	@Autowired
	private IResetPasswordTokenRepository resetPasswordTokenRepo;
	@Autowired
	private ApplicationEventPublisher publisher;

	@Override
	public User getCurrentUser() {
		String username;
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (principal instanceof UserDetails)
			username = ((UserDetails) principal).getUsername();
		else
			username = principal.toString();
		return userRepo.findByUsername(username).orElse(User.builder().username("Anonymous").build());
	}

	@Override
	public boolean emailExisted(String email) {
		return userRepo.existsByEmail(email);
	}

	@Override
	public boolean usernameExisted(String username) {
		return userRepo.existsByUsername(username);
	}

	@Override
	public User save(User user) {
		return userRepo.save(user);
	}

	@Override
	public User findById(Long id) {
		return userRepo.findById(id).orElse(null);
	}

	@Override
	public User findByUsername(String username) {
		return userRepo.findByUsername(username).orElse(null);
	}

	@Override
	public boolean changePassword(FormChangePassword changePassword) {
		if (!changePassword.getNewPassword1().equals(changePassword.getNewPassword2()))
			return false;
		User user = getCurrentUser();
		if (!encoder.matches(changePassword.getCurrentPassword(), user.getPassword()))
			return false;
		user.setPassword(encoder.encode(changePassword.getNewPassword1()));
		userRepo.save(user);
		return true;
	}

	@Override
	public User updateInfo(User user) {
		try {
			User currentUser = getCurrentUser();
			return userRepo.save(mapperUpdateUser(currentUser, user));
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	private User mapperUpdateUser(User currentUser, User updateUser) {
		if (updateUser.getAvatar() != null)
			currentUser.setAvatar(updateUser.getAvatar());
		if (updateUser.getEmail() != null)
			currentUser.setEmail(updateUser.getEmail());
		return currentUser;
	}

	@Override
	public String verifyTokenFromEmail(String token) {
		Optional<VerifyEmailToken> verifyEmailTokenOp = verifyEmailTokenRepo.findByToken(token);
		if (verifyEmailTokenOp.isEmpty())
			throw new ObjectNotFoundExeption("Token was not found!");
		VerifyEmailToken verifyEmailToken = verifyEmailTokenOp.get();
		if (verifyEmailToken.getExpirationDate().before(new Timestamp(System.currentTimeMillis())))
			throw new ExpiredTokenException("Expired confirmed email token!");
		
		User user = verifyEmailToken.getUser();
		verifyEmailToken.resetToken();
		user.setVerifiedEmail(true);
		userRepo.save(user);
		verifyEmailTokenRepo.save(verifyEmailToken);
		return "Verified email successfully";
	}

	@Override
	public void resendVerifyEmail(String token, String receiveUrl) {
		Optional<VerifyEmailToken> optional = verifyEmailTokenRepo.findByToken(token);
		if (optional.isEmpty())
			throw new ObjectNotFoundExeption("Token was not found!");
		VerifyEmailToken verifyEmailToken = optional.get();
		User user = verifyEmailToken.getUser();
		publisher.publishEvent(new VerifyEmailEvent(user, receiveUrl));
	}

	@Override
	public void handleUsernameAndEmailBeforResetPassword(String username, String email, String url) {
		Optional<User> optionalUser = userRepo.findByUsername(username);
		if (optionalUser.isEmpty())
			throw new ObjectNotFoundExeption("Username does not exist!");
		User user = optionalUser.get();
		if (!user.getEmail().equalsIgnoreCase(email))
			throw new ObjectNotFoundExeption("Email does not match username!");
		if(!user.isVerifiedEmail())
			throw new ObjectNotFoundExeption("Email is not verified!");
		publisher.publishEvent(new ResetPasswordEvent(user, url));
	}

	@Override
	public void verifyResetPasswordTokenFromEmail(String token) {
		Optional<ResetPasswordToken> opToken = resetPasswordTokenRepo.findByToken(token);
		if (opToken.isEmpty())
			throw new ObjectNotFoundExeption("Token was not found!");
		ResetPasswordToken resetPasswordToken = opToken.get();
		if (resetPasswordToken.getExpirationDate().before(new Timestamp(System.currentTimeMillis())))
			throw new ExpiredTokenException("Token was expired!");
		resetPasswordToken.resetToken();
		resetPasswordTokenRepo.save(resetPasswordToken);
		publisher.publishEvent(new ReturnNewPasswordEvent(resetPasswordToken.getUser()));
	}

	@Override
	public void resendEmailForResetPassword(String token, String receiveUrl) {
		Optional<ResetPasswordToken> optionalToken = resetPasswordTokenRepo.findByToken(token);
		if (optionalToken.isEmpty())
			throw new ObjectNotFoundExeption("Token was not found!");
		ResetPasswordToken resetPasswordToken = optionalToken.get();
		User user = resetPasswordToken.getUser();
		publisher.publishEvent(new ResetPasswordEvent(user, receiveUrl));
	}
	

}
















