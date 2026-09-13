package com.yashpal.response;

import com.yashpal.dto.OrderHistory;
import com.yashpal.model.Cart;
import com.yashpal.model.Product;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FunctionResponse {
    private String functionName;
    private Cart userCart;
    private OrderHistory orderHistory;
    private Product product;

 // Complete original Gemini functionCall JSON
    // Isme thoughtSignature aur functionCallId dono preserve rahenge
    private String originalFunctionCall;

    // Normal Gemini text response
    private String text;
}
