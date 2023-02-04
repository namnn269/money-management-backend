package com.nam.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class SocialAuthController {
	
	public ResponseEntity<?> googleLogin(){
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
