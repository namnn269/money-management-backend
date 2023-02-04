package com.nam.service.impl;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nam.dto.response.ComparisonChartDto;
import com.nam.dto.response.PieCategoryChartDto;
import com.nam.entity.User;
import com.nam.repository.ITransactionRepository;
import com.nam.service.IChartService;
import com.nam.service.IUserService;

@Service
public class ChartServiceImpl implements IChartService {

	@Autowired
	private ITransactionRepository transactionRepo;
	@Autowired 
	private IUserService userService;

	@Override
	public List<ComparisonChartDto> findDataForComparisonChart(Date startDate, Date endDate, String timeFilter) {

		User user=userService.getCurrentUser();
		Long userId=user.getId();
		List<ComparisonChartDto> comparisonDataDtos = new ArrayList<>();

		switch (timeFilter) {
		case "day":
			comparisonDataDtos = findDataForDaylyComparisonChart(userId,startDate, endDate);
			break;
		case "week":
			comparisonDataDtos = findDataForWeeklyComparisonChart(userId,startDate, endDate);
			break;
		case "month":
			comparisonDataDtos = findDataForMonthlyComparisonChart(userId,startDate, endDate);
			break;
		case "year":
			comparisonDataDtos = findDataForYearlyComparisonChart(userId,startDate, endDate);
			break;
		default:
			break;
		}
		return comparisonDataDtos;
	}
	
	@Override
	public List<ComparisonChartDto> findDataForDaylyComparisonChart(Long userId,Date startDate, Date endDate) {
		List<Object[]> incomeDaylyData = transactionRepo.findDataForDaylyComparisonChart(userId,1l, startDate, endDate);
		List<Object[]> expenseDaylyData = transactionRepo.findDataForDaylyComparisonChart(userId,2l, startDate, endDate);
		List<ComparisonChartDto> daylyChartDtos=mapperFromRawDataToChartData(incomeDaylyData, expenseDaylyData);
		return daylyChartDtos;
	}

	@Override
	public List<ComparisonChartDto> findDataForWeeklyComparisonChart(Long userId,Date startDate, Date endDate) {
		List<Object[]> incomeWeeklyData = transactionRepo.findDataForWeeklyComparisonChart(userId,1l, startDate, endDate);
		List<Object[]> expenseWeeklyData = transactionRepo.findDataForWeeklyComparisonChart(userId,2l, startDate, endDate);
		List<ComparisonChartDto> weelyChartDtos = mapperFromRawDataToChartData(incomeWeeklyData, expenseWeeklyData);
		return weelyChartDtos;
	}

	@Override
	public List<ComparisonChartDto> findDataForMonthlyComparisonChart(Long userId,Date startDate, Date endDate) {
		List<Object[]> incomeMonthlyData=transactionRepo.findDataForMonthlyComparisonChart(userId,1l, startDate, endDate);
		List<Object[]> expenseMonthlyData=transactionRepo.findDataForMonthlyComparisonChart(userId,2l, startDate, endDate);
		List<ComparisonChartDto> monthlyChartDtos=mapperFromRawDataToChartData(incomeMonthlyData, expenseMonthlyData);
		return monthlyChartDtos;
	}

	@Override
	public List<ComparisonChartDto> findDataForYearlyComparisonChart(Long userId,Date startDate, Date endDate) {
		List<Object[]> incomeYearlyData=transactionRepo.findDataForYearlyComparisonChart(userId,1l, startDate, endDate);
		List<Object[]> expenseYearlyData=transactionRepo.findDataForYearlyComparisonChart(userId,2l, startDate, endDate);
		List<ComparisonChartDto> yearlyChartDtos=mapperFromRawDataToChartData(incomeYearlyData, expenseYearlyData);
		return yearlyChartDtos;
	}
	private List<ComparisonChartDto> mapperFromRawDataToChartData(
			List<Object[]> incomeData,
			List<Object[]> expenseData) {
		Set<Object> timePointSet = new TreeSet<>();
		List<ComparisonChartDto> list = new ArrayList<>();

		expenseData.forEach(expense -> {
			timePointSet.add(expense[0]);
		});
		incomeData.forEach(income -> {
			timePointSet.add(income[0]);
		});
		
		timePointSet.forEach(timePoint -> {
			ComparisonChartDto chartDto = new ComparisonChartDto();
			chartDto.setTimePoint(timePoint);
			
			for (int i = 0; i < incomeData.size(); i++) {
				if (Objects.equals(incomeData.get(i)[0], timePoint)) {
					chartDto.setIncomeAmount(incomeData.get(i)[1]);
					break;
				} else {
					chartDto.setIncomeAmount(0);
				}
			}

			for (int i = 0; i < expenseData.size(); i++) {
				if (Objects.equals(expenseData.get(i)[0], timePoint)) {
					chartDto.setExpenseAmount(expenseData.get(i)[1]);
					break;
				} else {
					chartDto.setExpenseAmount(0);
				}
			}
			list.add(chartDto);
		});
		return list;
	}

	@Override
	public List<PieCategoryChartDto> findDataForPieChart(Date startDate, Date endDate, int categoryStatusId) {
		
		Long userId=userService.getCurrentUser().getId();
		List<Object[]> objList=transactionRepo.findDataForPieChart(userId, categoryStatusId, startDate,endDate);
		
		List<PieCategoryChartDto> pieChartDtos=new ArrayList<>();
		objList.forEach(obj->{
			PieCategoryChartDto pieDto=new PieCategoryChartDto();
			pieDto.setCategoryId(obj[0]);
			pieDto.setCategoryName((String) obj[1]);
			pieDto.setCategoryTotalAmount(obj[2]);
			pieDto.setCategoryColor((String)obj[3]);
			pieChartDtos.add(pieDto);
		});
		
		return pieChartDtos;
	}
}




