package com.nam.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PieCategoryChartDto {
	private Object categoryId;
	private String categoryName;
	private Object categoryTotalAmount; 
	private String categoryColor;
}
