package com.yashpal.service;

import com.yashpal.exception.ProductException;
import com.yashpal.model.Cart;
import com.yashpal.model.CartItem;
import com.yashpal.model.Product;
import com.yashpal.model.User;

public interface CartService {
	
	public CartItem addCartItem(User user,
								Product product,
								String size,
								int quantity) throws ProductException;
	
	public Cart findUserCart(User user);

}
