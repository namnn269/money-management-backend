package com.nam.controller;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nam.dto.request.TransactionReq;
import com.nam.dto.response.TotalAmountDto;
import com.nam.dto.response.TransactionResponse;
import com.nam.service.ITransactionService;

@RestController
@CrossOrigin
@RequestMapping(value = "/api/v1/transactions")
public class TransactionController {

	@Autowired
	private ITransactionService transactionService;

	@PostMapping
	public ResponseEntity<?> saveTransaction(@RequestBody TransactionReq transactionReq) {
		System.out.println(transactionReq);
		TransactionResponse transaction = transactionService.save(transactionReq);
		return new ResponseEntity<>(transaction, HttpStatus.CREATED);
	}

	@GetMapping
	public ResponseEntity<?> getTransactions(
			@RequestParam(name = "categorySelectedIds", defaultValue = "" ) Long[] categorySelectedIds,
			@RequestParam(name = "categoryStatusIds", defaultValue = "") Integer[] categoryStatusIds,
			@RequestParam(name = "startDate") Date startDate,
			@RequestParam(name = "endDate") Date endDate,
			@RequestParam(name = "pageSize") int pageSize,
			@RequestParam(name = "pageNo", defaultValue = "1") int pageNo) {
		if(pageNo <= 0) pageNo = 1;
		System.out.println(pageNo);
		Map<String, Object> responses = transactionService
				.findByPageAndTime(categorySelectedIds,categoryStatusIds,startDate, endDate,pageSize, pageNo);
		return new ResponseEntity<>(responses,HttpStatus.OK);
	}
	
	@DeleteMapping(value = "/{id}")
	public ResponseEntity<?> deleteTransaction(@PathVariable("id") Long id) {
		boolean isDeleted = transactionService.deleteById(id);
		if (isDeleted)
			return new ResponseEntity<>("Delete successfully!", HttpStatus.OK);
		else
			return new ResponseEntity<>("Delete failed!", HttpStatus.NOT_FOUND);
	}
	
	@GetMapping(value = "/total")
	public ResponseEntity<?> getTotalIncomeAndExpense(){
		TotalAmountDto totalAmount=transactionService.findTotalAmount();
		return new ResponseEntity<>(totalAmount,HttpStatus.OK);
	}

	@GetMapping(value = "/recent")
	public ResponseEntity<?> getRecentTransactions(@RequestParam("quantity") int quantity){
 		List<TransactionResponse> responses= transactionService.findRecentTransList(quantity);
		return new ResponseEntity<>(responses,HttpStatus.OK);
	}
}
