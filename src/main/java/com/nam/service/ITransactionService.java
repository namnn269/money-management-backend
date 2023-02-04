package com.nam.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.nam.dto.request.TransactionReq;
import com.nam.dto.response.TotalAmountDto;
import com.nam.dto.response.TransactionResponse;

public interface ITransactionService {

	TransactionResponse save(TransactionReq transactionReq);

	Map<String ,Object> findByPageAndTime(Long[] categoryList, Integer[] categoryStatusIds,
			Date startDate, Date endDate, int pageSize, int pageNo);

	boolean existsById(Long id);
	
	boolean deleteById(Long id);

	TotalAmountDto findTotalAmount();

	List<TransactionResponse> findRecentTransList(int quantity);
}
