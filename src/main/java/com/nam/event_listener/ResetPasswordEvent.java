package com.nam.event_listener;

import org.springframework.context.ApplicationEvent;

import com.nam.entity.User;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ResetPasswordEvent extends ApplicationEvent {

	private static final long serialVersionUID = 1L;

	private User user;

	private String url;

	public ResetPasswordEvent(User user) {
		super(user);
		this.user = user;
	}

	public ResetPasswordEvent(User user, String url) {
		super(user);
		this.user = user;
		this.url = url;
	}

}
