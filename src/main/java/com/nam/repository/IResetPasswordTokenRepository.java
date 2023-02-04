package com.nam.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nam.entity.ResetPasswordToken;
import com.nam.entity.User;

@Repository
public interface IResetPasswordTokenRepository extends JpaRepository<ResetPasswordToken, Long> {

	Optional<ResetPasswordToken> findByUser(User user);
	
	Optional<ResetPasswordToken> findByToken(String token);

}
