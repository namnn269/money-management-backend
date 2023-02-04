package com.nam.service.impl;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nam.entity.RefreshToken;
import com.nam.entity.User;
import com.nam.repository.IRefreshTokenRepository;
import com.nam.service.IRefreshTokenService;

@Service
@Transactional
public class RefreshTokenImpl implements IRefreshTokenService {

	@Autowired
	private IRefreshTokenRepository refreshTokenRepo;

	@Override
	public RefreshToken save(RefreshToken refreshToken) {
		return refreshTokenRepo.save(refreshToken);
	}

	@Override
	public RefreshToken findById(Long id) {
		return refreshTokenRepo.findById(id).orElse(null);
	}

	@Override
	public boolean existsById(Long refreshTokenID) {
		return refreshTokenRepo.existsById(refreshTokenID);
	}

	@Override
	public void deleteById(Long id) {
		System.out.println("delete refresh token: "+ id);
		refreshTokenRepo.deleteById(id);
	}

	@Override
	public int deleteAllByUser(User user) {
		int delCount = refreshTokenRepo.deleteByUser(user);
		return delCount;
	}

}
