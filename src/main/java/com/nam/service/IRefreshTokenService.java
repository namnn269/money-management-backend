package com.nam.service;

import com.nam.entity.RefreshToken;
import com.nam.entity.User;

public interface IRefreshTokenService {
	
	RefreshToken save(RefreshToken refreshToken);

	RefreshToken findById(Long id);

	boolean existsById(Long refreshTokenID);

	void deleteById(Long id);

	int deleteAllByUser(User user);

}
