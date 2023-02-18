package com.nam.dto.request;


import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionReq {
	private Long id;
	
	private double amount;
	
	private Date date;
	
	private String description;

	private Long categoryId;
}
