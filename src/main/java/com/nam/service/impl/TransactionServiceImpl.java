package com.nam.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.nam.dto.request.TransactionReq;
import com.nam.dto.response.TotalAmountDto;
import com.nam.dto.response.TransactionResponse;
import com.nam.entity.Category;
import com.nam.entity.Transaction;
import com.nam.entity.User;
import com.nam.repository.ICategoryRepository;
import com.nam.repository.ITransactionRepository;
import com.nam.service.ITransactionService;
import com.nam.service.IUserService;

@Service
public class TransactionServiceImpl implements ITransactionService {

	@Autowired
	private IUserService userService;
	@Autowired
	private ITransactionRepository transactionRepo;
	@Autowired
	private ICategoryRepository categoryRepo;

	@Override
	public TransactionResponse save(TransactionReq transactionReq) {
		User user = userService.getCurrentUser();
		Category category = categoryRepo.findById(transactionReq.getCategoryId())
				.orElse(null);
		Transaction transaction = Transaction.builder()
				.id(transactionReq.getId())
				.amount(transactionReq.getAmount())
				.date(new java.sql.Date(transactionReq.getDate().getTime()))
				.description(transactionReq.getDescription())
				.category(category)
				.user(user)
				.build();
		transaction= transactionRepo.save(transaction);
		
		TransactionResponse response=mapperFromTransToTransRes(transaction);
		return response;
	}

	@Override
	public Map<String, Object> findByPageAndTime(
			Long[] categorySelectedIds, Integer[] categoryStatusIds,
			Date startDate, Date endDate, int pageSize, int pageNo) {
		List<TransactionResponse> transList = new ArrayList<>();
		User user=userService.getCurrentUser();
		Sort sortByDate = Sort.by("date").descending();
		Sort sortByCategory = Sort.by("category").descending().and(sortByDate);
		java.sql.Date startDateSql = new java.sql.Date(startDate.getTime());
		java.sql.Date endDateSql = new java.sql.Date(endDate.getTime());
		Page<Transaction> transPage;
		if (categorySelectedIds.length == 0 && (categoryStatusIds.length == 0 || categoryStatusIds.length == 2) ) {
			Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sortByDate);
			transPage = transactionRepo.findAllByDate(startDateSql, endDateSql, user,pageable);
		} 
		else if (categorySelectedIds.length == 0 && categoryStatusIds.length == 1){
			Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sortByDate);
			transPage = transactionRepo.findAllByDateAndCategoryStatus(categoryStatusIds[0], startDateSql, endDateSql,user, pageable);
		}
		else {
			Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sortByCategory);
			transPage = transactionRepo.findAllByDateAndCategory(startDateSql, endDateSql, categorySelectedIds,user, pageable);
		}

		transList = transPage
					.getContent()
					.stream()
					.map(this::mapperFromTransToTransRes)
					.collect(Collectors.toList());
		Map<String, Object> responses=new HashMap<>();
		responses.put("list", transList);
		responses.put("totalPages", transPage.getTotalPages());
		return responses;
	}

	@Override
	public boolean existsById(Long id) {
		return transactionRepo.existsById(id);
	}

	@Override
	public boolean deleteById(Long id) {
		Optional<Transaction> opTrans = transactionRepo.findById(id);
		if (opTrans.isEmpty())
			return false;
		Transaction transaction = opTrans.get();
		transaction.setUser(null);
		transaction.setCategory(null);
		transactionRepo.delete(transaction);
		return true;
	}

	@Override
	public TotalAmountDto findTotalAmount() {
		Long userId = userService.getCurrentUser().getId();
		List<Object[]> incomeData = transactionRepo.findTotalAmount(userId, 1);
		List<Object[]> expenseData = transactionRepo.findTotalAmount(userId, 2);
		
		TotalAmountDto totalAmountDto = new TotalAmountDto();
		if (incomeData.isEmpty())
			totalAmountDto.setTotalIncome(0);
		else
			totalAmountDto.setTotalIncome((double) incomeData.get(0)[0]);
		
		if (expenseData.isEmpty())
			totalAmountDto.setTotalExpense(0);
		else
			totalAmountDto.setTotalExpense((double) expenseData.get(0)[0]);

		return totalAmountDto;
	}

	@Override
	public List<TransactionResponse> findRecentTransList(int quantity) {
		User user = userService.getCurrentUser();
		Pageable pageable = PageRequest.of(0, quantity, Sort.by("date").descending());
		List<Transaction> transactions = transactionRepo.findRecentTransList(user, pageable);
		List<TransactionResponse> responses = new ArrayList<>();
		transactions.forEach(trans -> {
			responses.add(mapperFromTransToTransRes(trans));
		});

		return responses;
	}
	
	private TransactionResponse mapperFromTransToTransRes(Transaction transaction) {
		TransactionResponse response = TransactionResponse.builder()
				.id(transaction.getId())
				.amount(transaction.getAmount())
				.date(transaction.getDate())
				.description(transaction.getDescription())
				.categoryId(transaction.getCategory().getId())
				.build();
		return response;
	}
	
}











