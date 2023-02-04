package com.nam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

@SpringBootApplication
@Component
public class MoneyManagementBackendApplication {

	@Autowired
	
	public static void main(String[] args) {
		SpringApplication.run(MoneyManagementBackendApplication.class, args);
	}

}
