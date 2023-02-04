package com.nam.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nam.entity.Category;
import com.nam.entity.User;
import com.nam.exceptions.ObjectExistedExeption;
import com.nam.repository.ICategoryRepository;
import com.nam.service.ICategoryService;
import com.nam.service.IUserService;

@Service
public class CategoryServiceImpl implements ICategoryService {

	@Autowired
	private ICategoryRepository categoryRepo;
	@Autowired
	private IUserService userService;

	@Override
	public Category save(Category category) {
		User user=userService.getCurrentUser();
		try {
			category.setUser(user);
			category = categoryRepo.save(category);
		} catch (Exception e) {
			throw new ObjectExistedExeption("Category existed!!");
		}
		return category;
	}

	@Override
	public boolean deleteById(Long id) {
		Optional<Category> opCategory = categoryRepo.findById(id);
		if (opCategory.isPresent()) {
			Category category = opCategory.get();
			category.setUser(null);
			category.getTransactions().stream().forEach(trans -> trans.setUser(null));
			categoryRepo.delete(category);
			return true;
		}
		return false;
	}

	@Override
	public List<Category> findAll(User user) {
		return categoryRepo.findByUser(user);
	}

	@Override
	public boolean categoryExists(Long id) {
		return categoryRepo.existsById(id);
	}

}
