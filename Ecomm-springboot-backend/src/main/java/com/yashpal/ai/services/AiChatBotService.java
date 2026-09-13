package com.yashpal.ai.services;

import com.yashpal.exception.ProductException;
import com.yashpal.response.ApiResponse;

public interface AiChatBotService {

    ApiResponse aiChatBot(String prompt,Long productId,Long userId) throws ProductException;
}
