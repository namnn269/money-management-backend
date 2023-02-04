package com.nam.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.NoArgsConstructor;

@Entity
@Table(name = "verify_email_token")
@NoArgsConstructor
public class VerifyEmailToken extends UserToken {
	
	public VerifyEmailToken(User user) {
		super(user);
	}
	
	public VerifyEmailToken(User user, long expiration) {
		super(user, expiration);
	}

}
