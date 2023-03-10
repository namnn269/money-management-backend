package com.nam.dto.response;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionResponse {
	private Long id;
	
	private double amount;
	
	private Date date;
	
	private String description;

	private Long categoryId;
}
