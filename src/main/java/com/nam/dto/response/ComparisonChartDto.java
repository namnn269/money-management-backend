package com.nam.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ComparisonChartDto {
	private Object incomeAmount;
	private Object expenseAmount;
	private Object timePoint;
}
