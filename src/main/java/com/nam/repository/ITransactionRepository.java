package com.nam.repository;

import java.sql.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

import com.nam.entity.Transaction;
import com.nam.entity.User;

@Repository
public interface ITransactionRepository extends JpaRepository<Transaction, Long>{
	
	@Query(value = "SELECT t FROM Transaction t"
			+ " JOIN t.category c "
			+ " WHERE c.id IN :categoryIds AND t.date >= :startDate AND t.date <= :endDate AND t.user = :user " )
	Page<Transaction> findAllByDateAndCategory(
			@RequestParam("startDate") Date startDate,
			@RequestParam("endDate") Date endDate,
			@RequestParam("categoryIds") Long[] categoryIds, 
			@RequestParam("user") User user, 
			Pageable pageable);

	@Query(value = " SELECT t FROM Transaction t"
			+ " JOIN t.category c"
			+ " WHERE c.categoryStatusId = :categoryStatusId "
			+ "		AND t.date >= :startDate AND t.date <= :endDate AND t.user = :user  ")
	Page<Transaction> findAllByDateAndCategoryStatus(
			@RequestParam("categoryStatusId") Integer categoryStatusId, 
			@RequestParam("date") Date startDate,
			@RequestParam("date") Date endDate,
			@RequestParam("user") User user, 
			Pageable pageable);

	@Query(value = " SELECT t FROM Transaction t"
			+ " WHERE t.date >= :startDate AND t.date <= :endDate AND t.user = :user  ")
	Page<Transaction> findAllByDate(
			@RequestParam("startDate") Date startDate,
			@RequestParam("endDate") Date endDate ,
			@RequestParam("user") User user, 
			Pageable pageable);
	
	@Query(value = "SELECT DATE(date) as day, "
			+ " SUM(amount) as total "
			+ " FROM transactions t "
			+ " JOIN categories c ON c.id = t.category_id "
			+ " JOIN users u ON u.id = c.user_id "
			+ " WHERE c.category_status_id = :categoryStatusId AND t.date >= :startDate AND t.date <= :endDate AND u.id = :userId "
			+ " GROUP BY day "
			+ " ORDER BY day ;",nativeQuery = true)
	List<Object[]> findDataForDaylyComparisonChart(
			@RequestParam("userId") Long userId,
			@RequestParam("categoryStatusId") Long categoryStatusId,
			@RequestParam("startDate") Date startDate,
			@RequestParam("endDate") Date endDate);
	
	@Query(value = "SELECT YEARWEEK(date) as week, "
			+ " SUM(amount) as total "
			+ " FROM transactions t "
			+ " JOIN categories c ON c.id = t.category_id "
			+ " JOIN users u ON u.id = c.user_id "
			+ " WHERE c.category_status_id = :categoryStatusId AND t.date >= :startDate AND t.date <= :endDate AND u.id = :userId "
			+ " GROUP BY week "
			+ " ORDER BY week desc ;",nativeQuery = true)
	List<Object[]> findDataForWeeklyComparisonChart(
			@RequestParam("userId") Long userId,
			@RequestParam("categoryStatusId") Long categoryStatusId,
			@RequestParam("startDate") Date startDate,
			@RequestParam("endDate") Date endDate);
	
	@Query(value = "SELECT DATE_FORMAT(date,'%m-%Y') as month, "
			+ " SUM(amount) as total "
			+ " FROM transactions t "
			+ " JOIN categories c ON c.id = t.category_id "
			+ " JOIN users u ON u.id = c.user_id "
			+ " WHERE c.category_status_id = :categoryStatusId AND t.date >= :startDate AND t.date <= :endDate AND u.id = :userId "
			+ " GROUP BY month "
			+ " ORDER BY month ;",nativeQuery = true)
	List<Object[]> findDataForMonthlyComparisonChart(
			@RequestParam("userId") Long userId,
			@RequestParam("categoryStatusId") Long categoryStatusId,
			@RequestParam("startDate") Date startDate,
			@RequestParam("endDate") Date endDate);
	
	@Query(value = "SELECT DATE_FORMAT(date,'%Y') as year, "
			+ " SUM(amount) as total "
			+ " FROM transactions t "
			+ " JOIN categories c ON c.id = t.category_id "
			+ " JOIN users u ON u.id = c.user_id "
			+ " WHERE c.category_status_id = :categoryStatusId AND t.date >= :startDate AND t.date <= :endDate AND u.id = :userId "
			+ " GROUP BY year "
			+ " ORDER BY year ;",nativeQuery = true)
	List<Object[]> findDataForYearlyComparisonChart(
			@RequestParam("userId") Long userId,
			@RequestParam("categoryStatusId") Long categoryStatusId,
			@RequestParam("startDate") Date startDate,
			@RequestParam("endDate") Date endDate);
	
	@Query(value = " SELECT c.id, c.name, SUM(t.amount) as total, c.color "
			+ " FROM transactions t "
			+ " JOIN categories c ON c.id=t.category_id "
			+ " JOIN users u ON u.id = c.user_id "
			+ " WHERE c.category_status_id = :categoryStatusId AND u.id = :userId AND t.date >= :startDate AND t.date <= :endDate "
			+ " GROUP BY c.id "
			+ " ORDER BY total desc;", nativeQuery = true)
	List<Object[]> findDataForPieChart(
			@RequestParam Long userId,
			@RequestParam int categoryStatusId,
			@RequestParam Date startDate,
			@RequestParam Date endDate);

	@Query(value = " SELECT sum(amount)"
			+ " FROM transactions t"
			+ " JOIN categories c ON c.id = t.category_id"
			+ " WHERE c.category_status_id = :categoryStatusId AND t.user_id = :userId"
			+ " GROUP BY t.user_id ",nativeQuery = true)
	List<Object[]> findTotalAmount(
			@RequestParam("userId") Long userId,
			@RequestParam("categoryStatusId") int categoryStatusId);

	@Query(value = "SELECT t FROM Transaction t "
			+ " WHERE t.user = :user")
	List<Transaction> findRecentTransList(
			@RequestParam("user") User user, Pageable pageable);
	
}





















