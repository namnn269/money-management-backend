package com.nam.service;

import java.sql.Date;
import java.util.List;

import com.nam.dto.response.ComparisonChartDto;
import com.nam.dto.response.PieCategoryChartDto;

public interface IChartService {

	List<ComparisonChartDto> findDataForComparisonChart(Date startDate, Date endDate, String timeFilter);

	List<ComparisonChartDto> findDataForDaylyComparisonChart(Long userId, Date startDate, Date endDate);

	List<ComparisonChartDto> findDataForWeeklyComparisonChart(Long userId, Date startDate, Date endDate);

	List<ComparisonChartDto> findDataForMonthlyComparisonChart(Long userId, Date startDate, Date endDate);

	List<ComparisonChartDto> findDataForYearlyComparisonChart(Long userId, Date startDate, Date endDate);

	List<PieCategoryChartDto> findDataForPieChart(Date startDate, Date endDate, int categoryStatusId);
}
