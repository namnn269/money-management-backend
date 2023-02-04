package com.nam.service;

import java.util.List;

import com.nam.entity.Category;
import com.nam.entity.User;

public interface ICategoryService {
	
	Category save(Category category);
	
	boolean deleteById(Long id);
	
	List<Category> findAll(User user);
	
	boolean categoryExists(Long id);
	
}
