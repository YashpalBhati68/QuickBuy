package com.yashpal.service;

import com.yashpal.exception.UserException;
import com.yashpal.model.User;

public interface UserService {

	public User findUserProfileByJwt(String jwt) throws UserException;
	
	public User findUserByEmail(String email) throws UserException;


}
