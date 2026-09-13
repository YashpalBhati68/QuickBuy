package com.yashpal.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yashpal.model.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

}
