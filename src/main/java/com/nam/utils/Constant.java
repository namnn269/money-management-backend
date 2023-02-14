package com.nam.utils;

public class Constant {
	public static final String FRONTEND_URL = "http://localhost:3000";
	public static final String FRONTEND_URL_VERIFY_EMAIL_RESPONSE = FRONTEND_URL + "/verify-email-response";
	
	public static final int VERIFY_EMAIL_TOKEN_EXPIRATION = 5 * 60;// seconds
	public static final int RESET_PASSWORD_TOKEN_EXPIRATION = 60;
}
