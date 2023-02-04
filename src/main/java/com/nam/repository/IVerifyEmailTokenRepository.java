package com.nam.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nam.entity.User;
import com.nam.entity.VerifyEmailToken;

@Repository
public interface IVerifyEmailTokenRepository extends JpaRepository<VerifyEmailToken, Long> {
	Optional<VerifyEmailToken> findByToken(String token);

	Optional<VerifyEmailToken> findByUser(User user);
}
