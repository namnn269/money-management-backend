package com.nam.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TotalAmountDto {

	private double totalIncome;
	private double totalExpense;
	
}
