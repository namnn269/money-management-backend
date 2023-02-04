package com.nam.service;

import com.nam.dto.server.EmailDto;

public interface IEmailService {
	String sendEmail(EmailDto email);
}
