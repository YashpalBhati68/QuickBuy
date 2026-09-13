package com.yashpal.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yashpal.model.Cart;

public interface CartRepository extends JpaRepository<Cart, Long> {

	 Cart findByUserId(Long userId);
}
