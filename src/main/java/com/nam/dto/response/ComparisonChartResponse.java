package com.nam.dto.response;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ComparisonChartResponse {
	private String timeFilter;
	private Date startDate;
	private Date endDate;
	private List<ComparisonChartDto> chartData;
}
