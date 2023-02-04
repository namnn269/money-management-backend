package com.nam.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nam.entity.RefreshToken;
import com.nam.entity.User;

@Repository
@Transactional
public interface IRefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
	
	int deleteByUser( User user);
	
}
