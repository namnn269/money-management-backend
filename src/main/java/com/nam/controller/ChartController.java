package com.nam.controller;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nam.dto.response.ComparisonChartDto;
import com.nam.dto.response.ComparisonChartResponse;
import com.nam.dto.response.PieCategoryChartDto;
import com.nam.service.IChartService;

@RestController
@CrossOrigin
@RequestMapping(value = "/api/v1")
public class ChartController {

	@Autowired
	private IChartService chartService;

	@GetMapping(value = "/chart-comparison")
	public ResponseEntity<?> getValueForChartComparison(
			@RequestParam(name = "startDate") Date startDate,
			@RequestParam(name = "endDate") Date endDate, 
			@RequestParam(name = "timeFilter") String timeFilter) {
		java.sql.Date startDateSql = new java.sql.Date(startDate.getTime());
		java.sql.Date endDateSql = new java.sql.Date(endDate.getTime());
		List<ComparisonChartDto> data = chartService.findDataForComparisonChart(startDateSql, endDateSql, timeFilter);
		return new ResponseEntity<>(new ComparisonChartResponse(timeFilter,startDate,endDate, data), HttpStatus.OK);
	}
	
	@GetMapping(value = "/chart-pie")
	public ResponseEntity<?> getValueForPieChart(
			@RequestParam(name = "startDate") Date startDate,
			@RequestParam(name = "endDate") Date endDate,
			@RequestParam (name = "categoryStatusId") int categoryStatusId){

		java.sql.Date startDateSql = new java.sql.Date(startDate.getTime());
		java.sql.Date endDateSql = new java.sql.Date(endDate.getTime());
		List<PieCategoryChartDto> pieChartDtos = chartService.findDataForPieChart(startDateSql, endDateSql, categoryStatusId);
		return new ResponseEntity<>(pieChartDtos,HttpStatus.OK);
	}
}
