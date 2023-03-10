package com.nam.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nam.entity.Category;
import com.nam.entity.User;

@Repository
public interface ICategoryRepository extends JpaRepository<Category, Long>{
	
	List<Category> findByUser(User user);
	
}
