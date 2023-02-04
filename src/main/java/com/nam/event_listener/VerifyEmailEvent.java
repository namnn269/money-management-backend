package com.nam.event_listener;

import org.springframework.context.ApplicationEvent;

import com.nam.entity.User;

import lombok.Getter;

@Getter
public class VerifyEmailEvent extends ApplicationEvent {

	private static final long serialVersionUID = 1L;
	private User user;
	private String url;

	public VerifyEmailEvent(User user, String url) {
		super(user);
		this.user = user;
		this.url = url;
	}

}
