package com.nam.entity;

import java.sql.Timestamp;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToOne;

import lombok.Data;

@Data
@MappedSuperclass
public class UserToken {
	private long EXPIRATION = 60l; // seconds

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "token")
	private String token;

	@Column(name = "expiration_date")
	private Timestamp expirationDate;

	@OneToOne
	@JoinColumn(name = "user_id")
	private User user;
	
	public UserToken() {
		this.expirationDate = calcExpirationDate();
		this.token = UUID.randomUUID().toString();
	}

	public UserToken(User user) {
		this.user = user;
		this.expirationDate = calcExpirationDate();
		this.token = UUID.randomUUID().toString();
	}

	public UserToken(User user, long expiration) {
		this.user = user;
		this.EXPIRATION = expiration;
		this.expirationDate = calcExpirationDate();
		this.token = UUID.randomUUID().toString();
	}

	public void resetToken() {
		this.expirationDate = calcExpirationDate();
		this.token = UUID.randomUUID().toString();
	}

	private Timestamp calcExpirationDate() {
		return new Timestamp(System.currentTimeMillis() + this.EXPIRATION * 1000);
	}

}
