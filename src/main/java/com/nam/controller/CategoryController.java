package com.nam.controller;

import java.util.List;

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
import org.springframework.web.bind.annotation.RestController;

import com.nam.dto.response.ResponseMessage;
import com.nam.entity.Category;
import com.nam.entity.User;
import com.nam.exceptions.ObjectExistedExeption;
import com.nam.service.ICategoryService;
import com.nam.service.IUserService;

@RestController
@CrossOrigin
@RequestMapping(value = "/api/v1/categories")
public class CategoryController {

	@Autowired
	private IUserService userService;

	@Autowired
	private ICategoryService categoryService;

	@PostMapping
	public ResponseEntity<?> save(@RequestBody Category categoryReq) {
		System.out.println(categoryReq);
		User user = userService.getCurrentUser();
		if (user.getUsername().equals("Anonymous"))
			return new ResponseEntity<>(new ResponseMessage("Please login!"), HttpStatus.UNAUTHORIZED);
		categoryReq.setUser(user);
		try {
			Category category = categoryService.save(categoryReq);
			return new ResponseEntity<>(category, HttpStatus.CREATED);
		} catch (ObjectExistedExeption e) {
			System.out.println(e.getMessage());
			return new ResponseEntity<>(new ResponseMessage(e.getMessage()), HttpStatus.CONFLICT);
		}
	}

	@DeleteMapping(value = "/{id}")
	public ResponseEntity<?> delete(@PathVariable(name = "id") Long id) {
		boolean deleteFinish = categoryService.deleteById(id);
		if (deleteFinish)
			return new ResponseEntity<>(new ResponseMessage("Delete successfully!"), HttpStatus.OK);
		else
			return new ResponseEntity<>(new ResponseMessage("Delete failed!"), HttpStatus.CONFLICT);
	}

	@GetMapping
	public ResponseEntity<?> getAll() {
		User user = userService.getCurrentUser();
		List<Category> categories = categoryService.findAll(user);
		return new ResponseEntity<>(categories, HttpStatus.OK);
	}

}
