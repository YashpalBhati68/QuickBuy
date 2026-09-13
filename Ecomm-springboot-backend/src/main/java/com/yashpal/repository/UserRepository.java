package com.yashpal.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yashpal.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

	
	public User findByEmail(String username);

}
