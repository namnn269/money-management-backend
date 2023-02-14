package com.nam.controller;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import com.nam.dto.request.FormChangePassword;
import com.nam.dto.request.FormLogin;
import com.nam.dto.request.FormSignup;
import com.nam.dto.request.RefreshTokenReq;
import com.nam.dto.response.LoginUserResponse;
import com.nam.dto.response.RefreshTokenResponse;
import com.nam.dto.response.ResponseMessage;
import com.nam.dto.response.UpdatedUserResponse;
import com.nam.entity.RefreshToken;
import com.nam.entity.Role;
import com.nam.entity.RoleName;
import com.nam.entity.User;
import com.nam.event_listener.VerifyEmailEvent;
import com.nam.exceptions.ExpiredTokenException;
import com.nam.exceptions.ObjectNotFoundExeption;
import com.nam.repository.IRoleRepository;
import com.nam.security.jwt.JwtProvider;
import com.nam.security.principal.UserDetailImpl;
import com.nam.service.IRefreshTokenService;
import com.nam.service.IUserService;
import com.nam.utils.Constant;
import com.nam.utils.RequestUrl;

@RestController
@CrossOrigin
//@CrossOrigin(origins = "http://localhost:3000",allowedHeaders = "*")
@RequestMapping(value = "/api/v1/auth")
public class AuthController {

	@Autowired
	private IUserService userService;
	@Autowired
	private IRefreshTokenService refreshTokenService;
	@Autowired
	private IRoleRepository roleRepo;
	@Autowired
	private PasswordEncoder encoder;
	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private ApplicationEventPublisher publisher;
	@Autowired
	private JwtProvider jwtProvider;

	@PostMapping(value = "/signup")
	public ResponseEntity<?> signup(@RequestBody FormSignup formSignup, HttpServletRequest request) {
		if (userService.usernameExisted(formSignup.getUsername()))
			return new ResponseEntity<>(new ResponseMessage("Username has been existed!"), HttpStatus.CONFLICT);
		if (userService.emailExisted(formSignup.getEmail()))
			return new ResponseEntity<>(new ResponseMessage("Email has been existed!"), HttpStatus.CONFLICT);

		Set<Role> roles = new HashSet<>();
		Role roleUser = roleRepo.findByName(RoleName.USER).get(0);
		roles.add(roleUser);
		
		User user = User.builder()
				.email(formSignup.getEmail())
				.username(formSignup.getUsername())
				.password(encoder.encode(formSignup.getPassword()))
				.avatar(formSignup.getAvatar())
				.verifiedEmail(false)
				.roles(roles)
				.build();
		userService.save(user);
		return new ResponseEntity<>(new ResponseMessage("Register successfully"), HttpStatus.CREATED);
	}
	
	@PostMapping(value = "/login")
	public ResponseEntity<?> login(@RequestBody FormLogin formLogin){
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(formLogin.getUsername(), formLogin.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(authentication);
		UserDetailImpl userDetails = (UserDetailImpl) authentication.getPrincipal();
		User user = userDetails.getUser();
		
		RefreshToken refreshToken = RefreshToken.builder().user(user).build();
		refreshToken = refreshTokenService.save(refreshToken);
		
		String refreshTokenStr = jwtProvider.generateRefreshToken(user, refreshToken);
		String accessTokenStr = jwtProvider.generateAccessToken(user);
		Collection<String> roles = user.getRoles()
							.stream()
							.map(role -> role.getName().name())
							.collect(Collectors.toList());
		LoginUserResponse userResponse = LoginUserResponse.builder()
				.id(user.getId())
				.username(user.getUsername())
				.email(user.getEmail())
				.verifiedEmail(user.isVerifiedEmail())
				.avatar(user.getAvatar())
				.roles(roles)
				.accessToken(accessTokenStr)
				.refreshToken(refreshTokenStr)
				.build();

		return new ResponseEntity<>(userResponse, HttpStatus.ACCEPTED);
	}
	
	@PostMapping(value = "/refresh-token")
	public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenReq refreshTokenReq){
		String refreshTokenStr = refreshTokenReq.getRefreshToken();
		boolean validToken= jwtProvider.validateRefreshToken(refreshTokenStr);
		if (!validToken)
			return new ResponseEntity<>(new ResponseMessage("Refresh token is invalid 1!"), HttpStatus.UNAUTHORIZED);

		Long refreshTokenID = jwtProvider.getRefreshTokenIdFromRefreshToken(refreshTokenStr);
		boolean existsToken= refreshTokenService.existsById(refreshTokenID);
		
		if (!existsToken)
			return new ResponseEntity<>(new ResponseMessage("Refresh token is invalid 2!"), HttpStatus.UNAUTHORIZED);
		
		refreshTokenService.deleteById(refreshTokenID);

		User user = userService.findByUsername(jwtProvider.getUsernameFromRefreshToken(refreshTokenStr));
		RefreshToken newRefreshToken = refreshTokenService.save(RefreshToken.builder().user(user).build());

		String accessToken = jwtProvider.generateAccessToken(user);
		String refreshToken = jwtProvider.generateRefreshToken(user, newRefreshToken);
		
		RefreshTokenResponse refreshTokenResponse = new RefreshTokenResponse(refreshToken, accessToken);
		return  new ResponseEntity<>(refreshTokenResponse,HttpStatus.OK);
	}

	@PostMapping(value = "/logout")
	public ResponseEntity<?> logout(@RequestBody RefreshTokenReq refreshTokenReq){
		String refreshTokenStr=refreshTokenReq.getRefreshToken();
		boolean validToken = jwtProvider.validateRefreshToken(refreshTokenStr);
		if (!validToken)
			return new ResponseEntity<>(new ResponseMessage("Logout: Refresh token is invalid 1!"), HttpStatus.UNAUTHORIZED); 
		
		Long id = jwtProvider.getRefreshTokenIdFromRefreshToken(refreshTokenStr);
		boolean existsToken = refreshTokenService.existsById(id);
		if (!existsToken)
			return new ResponseEntity<>(new ResponseMessage("Logout: Refresh token is invalid 2!"), HttpStatus.UNAUTHORIZED); 
		
		refreshTokenService.deleteById(id);
		return new ResponseEntity<>(new ResponseMessage("Logged out successfully!"),HttpStatus.OK);
	}
	
	
	@PostMapping(value = "/logout-all")
	public ResponseEntity<?> logoutAll(@RequestBody RefreshTokenReq refreshTokenReq){
		
		String refreshTokenStr=refreshTokenReq.getRefreshToken();
		boolean validToken = jwtProvider.validateRefreshToken(refreshTokenStr);
		if (!validToken)
			return new ResponseEntity<>(new ResponseMessage("Logout-all: Refresh token is invalid 1!"), HttpStatus.UNAUTHORIZED); 
		
		Long id = jwtProvider.getRefreshTokenIdFromRefreshToken(refreshTokenStr);
		boolean existsToken=refreshTokenService.existsById(id);
		if (!existsToken)
			return new ResponseEntity<>(new ResponseMessage("Logout-all: Refresh token is invalid 2!"), HttpStatus.UNAUTHORIZED); 
		
		User user = userService.findByUsername(jwtProvider.getUsernameFromRefreshToken(refreshTokenStr));

		int delCount = refreshTokenService.deleteAllByUser(user);
		return new ResponseEntity<>(new ResponseMessage("Logged out from " + delCount + " devices successfully!"), HttpStatus.OK);
	}
	
	@PostMapping(value = "/updatePassword")
	public ResponseEntity<?> changePassword(@RequestBody FormChangePassword changePassword) {
		boolean changePassordOk = userService.changePassword(changePassword);
		if (changePassordOk)
			return new ResponseEntity<>("Changed password successfully!", HttpStatus.OK);
		else
			return new ResponseEntity<>("Current password is not correct!", HttpStatus.NOT_ACCEPTABLE);
	}
	
	@PostMapping(value = "/updateInfo")
	public ResponseEntity<?> changeInfo(@RequestBody User user) {
		User updatedUser = userService.updateInfo(user);
		try {
			return new ResponseEntity<>(mapperFromUserToUpdatedUserResponse(updatedUser), HttpStatus.ACCEPTED);
		} catch (Exception e) {
			return new ResponseEntity<>("Error when updating infomation!", HttpStatus.NOT_ACCEPTABLE);
		}
	} 

	@PostMapping(value = "/verifyEmail")
	public ResponseEntity<?> verifyEmail(@RequestParam(name = "email") String email, 
										HttpServletRequest request) {
		User user = userService.getCurrentUser();
		if(!email.equalsIgnoreCase(user.getEmail()) && userService.emailExisted(email)) {
			return new ResponseEntity<>("You have changed your email from the original "
					+ "and your new email is already in use by someone else.", HttpStatus.CONFLICT);
		}
		user.setEmail(email);
		String url = RequestUrl.getUrlFromRequest(request);
		publisher.publishEvent(new VerifyEmailEvent(user, url));
		return null;
	}
	
	@GetMapping(value = "/receive-token-from-email")
	public RedirectView receiveEmail(@RequestParam(name = "token") String token) {
		String redirectUrl = Constant.FRONTEND_URL_VERIFY_EMAIL_RESPONSE + "?type=verify_email";
		try {
			userService.verifyTokenFromEmail(token);
			redirectUrl += "&verified_email=true";
		} catch (ObjectNotFoundExeption e) {
			redirectUrl += "&message=" + e.getMessage();
		} catch (ExpiredTokenException e) {
			redirectUrl += "&verified_email=false&token=" + token + "&message=" + e.getMessage();
		} catch (Exception e) {
		}
		return new RedirectView(redirectUrl);
	}
	
	@GetMapping(value = "/resendEmail")
	public RedirectView resendEmail(@RequestParam(name = "token") String token,
				HttpServletRequest request){
		String redirectUrl = Constant.FRONTEND_URL_VERIFY_EMAIL_RESPONSE;
		String receiveUrl = RequestUrl.getUrlFromRequest(request);
		try {
			userService.resendVerifyEmail(token, receiveUrl);
			redirectUrl += "?message=Please check your email!";
		} catch (ObjectNotFoundExeption e) {
			redirectUrl += "?message=" + e.getMessage();
		} catch (Exception e) {
		}
		return new RedirectView(redirectUrl);
	}
	
	@PostMapping(value = "/resetPassword")
	public ResponseEntity<?> resetPassword( @RequestParam(name = "username") String username,
											@RequestParam(name = "email") String email,
											HttpServletRequest request){
		try {
			String url = RequestUrl.getUrlFromRequest(request);
			userService.handleUsernameAndEmailBeforResetPassword(username, email, url);
		} catch (ObjectNotFoundExeption e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
		} catch (Exception e) {
		}
		return new ResponseEntity<>("Please check your email!", HttpStatus.OK);
	}
	
	@GetMapping(value = "/receive-reset-password-token-from-email")
	public RedirectView verifyResetPasswordTokenFromEmail(@RequestParam(name = "token") String token) {
		String redirectUrl = Constant.FRONTEND_URL_VERIFY_EMAIL_RESPONSE + "?type=reset_password";
		try {
			userService.verifyResetPasswordTokenFromEmail(token);
			redirectUrl += "&verified_email=true";
		} catch (ObjectNotFoundExeption e) {
			redirectUrl += "&message=" + e.getMessage();
		} catch (ExpiredTokenException e) {
			redirectUrl += "&verified_email=false&message=" + e.getMessage() + "&token=" + token;
		} catch (Exception e) {
		}
		return new RedirectView(redirectUrl);
	}
	
	@GetMapping(value = "/resendEmailForResetPassword")
	public RedirectView resendEmailForResetPassword(@RequestParam(name = "token") String token,
													HttpServletRequest request){
		String url = RequestUrl.getUrlFromRequest(request);
		String redirectUrl = Constant.FRONTEND_URL_VERIFY_EMAIL_RESPONSE;
		try {
			userService.resendEmailForResetPassword(token, url);
			redirectUrl += "?message=Please check your email!";
		} catch (ObjectNotFoundExeption e) {
			redirectUrl += "?message=" + e.getMessage();
		} catch (Exception e) {
		}
		return new RedirectView(redirectUrl);
	}

	private UpdatedUserResponse mapperFromUserToUpdatedUserResponse(User user) {
		Collection<String> roles = user
				.getRoles()
				.stream()
				.map(role->role.getName().toString())
				.collect(Collectors.toList());
		UpdatedUserResponse userResponse = UpdatedUserResponse.builder()
				.id(user.getId())
				.avatar(user.getAvatar())
				.username(user.getUsername())
				.email(user.getEmail())
				.verifiedEmail(user.isVerifiedEmail())
				.roles(roles)
				.build();
		return userResponse;
	}
	
}































