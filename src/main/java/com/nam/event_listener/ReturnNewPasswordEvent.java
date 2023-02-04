package com.nam.event_listener;

import org.springframework.context.ApplicationEvent;

import com.nam.entity.User;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReturnNewPasswordEvent extends ApplicationEvent {

	private static final long serialVersionUID = 1L;
	private User user;

	public ReturnNewPasswordEvent(User user) {
		super(user);
		this.user = user;
	}

}
