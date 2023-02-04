package com.nam.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.NoArgsConstructor;

@Entity
@Table(name = "reset_password_token")
@NoArgsConstructor
public class ResetPasswordToken extends UserToken {
	public ResetPasswordToken(User user) {
		super(user);
	}

	public ResetPasswordToken(User user, long expiration) {
		super(user, expiration);
	}
}
