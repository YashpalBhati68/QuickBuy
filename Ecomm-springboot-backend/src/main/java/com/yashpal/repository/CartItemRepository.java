package com.yashpal.repository;

import com.yashpal.model.Cart;
import com.yashpal.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import com.yashpal.model.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {


    CartItem findByCartAndProductAndSize(Cart cart, Product product, String size);


}
