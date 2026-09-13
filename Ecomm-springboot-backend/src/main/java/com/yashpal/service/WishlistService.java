package com.yashpal.service;


import com.yashpal.exception.WishlistNotFoundException;
import com.yashpal.model.Product;
import com.yashpal.model.User;
import com.yashpal.model.Wishlist;

public interface WishlistService {

    Wishlist createWishlist(User user);

    Wishlist getWishlistByUserId(User user);

    Wishlist addProductToWishlist(User user, Product product) throws WishlistNotFoundException;

}

