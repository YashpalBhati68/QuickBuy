package com.yashpal.service;

import com.yashpal.exception.SellerException;
import com.yashpal.exception.UserException;
import com.yashpal.request.LoginRequest;
import com.yashpal.request.SignupRequest;
import com.yashpal.response.AuthResponse;
import jakarta.mail.MessagingException;

public interface AuthService {

    void sentLoginOtp(String email) throws UserException, MessagingException;
    String createUser(SignupRequest req) throws SellerException;
    AuthResponse signin(LoginRequest req) throws SellerException;

}
