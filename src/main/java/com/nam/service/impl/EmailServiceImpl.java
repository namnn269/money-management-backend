package com.nam.service.impl;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.nam.dto.server.EmailDto;
import com.nam.service.IEmailService;

@Service
public class EmailServiceImpl implements IEmailService {

	private Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);
	@Value("${email.from}")
	private String emailFrom;
	@Autowired
	private JavaMailSender javaMailSender;

	@Override
	public String sendEmail(EmailDto email) {

		MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		try {
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
			helper.setTo(email.getTo());
			helper.setFrom(emailFrom);
			helper.setSubject(email.getSubject());
			helper.setText(email.getContent(), true);
			javaMailSender.send(mimeMessage);
			return "Send email successfully!";
		} catch (MailException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (MessagingException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return "Error when sending email!";
	}

}
