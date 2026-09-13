
package com.yashpal.ai.services;

import com.yashpal.exception.ProductException;
import com.yashpal.model.Cart;
import com.yashpal.model.Product;
import com.yashpal.model.User;
import com.yashpal.dto.OrderHistory;
import com.yashpal.mapper.OrderMapper;
import com.yashpal.mapper.ProductMapper;
import com.yashpal.repository.CartRepository;
import com.yashpal.repository.OrderRepository;
import com.yashpal.repository.ProductRepository;
import com.yashpal.repository.UserRepository;
import com.yashpal.response.ApiResponse;
import com.yashpal.response.FunctionResponse;

import lombok.RequiredArgsConstructor;

import org.json.JSONArray;
import org.json.JSONObject;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AiChatBotServiceImpl implements AiChatBotService {

    private final CartRepository cartRepository;

    private final OrderRepository orderRepository;

    private final ProductRepository productRepository;

    private final UserRepository userRepository;

    @Value("${gemini.api.key}")
    private String GEMINI_API_KEY;


    // =====================================================
    // GEMINI API URL
    // =====================================================

    private String getGeminiApiUrl() {

        return "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.6-flash:generateContent?key="
                + GEMINI_API_KEY;

    }


    // =====================================================
    // FUNCTION DECLARATIONS
    // =====================================================

    private JSONArray createFunctionDeclarations() {

        JSONArray declarations = new JSONArray();


        // =================================================
        // GET USER CART
        // =================================================

        declarations.put(

                new JSONObject()

                        .put(
                                "name",
                                "getUserCart"
                        )

                        .put(
                                "description",
                                "Retrieve the user's cart details"
                        )

                        .put(
                                "parameters",
                                new JSONObject()

                                        .put(
                                                "type",
                                                "OBJECT"
                                        )

                                        .put(
                                                "properties",
                                                new JSONObject()

                                                        .put(
                                                                "cart",
                                                                new JSONObject()

                                                                        .put(
                                                                                "type",
                                                                                "STRING"
                                                                        )

                                                                        .put(
                                                                                "description",
                                                                                "Cart details"
                                                                        )

                                                        )

                                        )

                        )

        );


        // =================================================
        // GET USER ORDERS
        // =================================================

        declarations.put(

                new JSONObject()

                        .put(
                                "name",
                                "getUsersOrder"
                        )

                        .put(
                                "description",
                                "Retrieve the user's order details"
                        )

                        .put(
                                "parameters",
                                new JSONObject()

                                        .put(
                                                "type",
                                                "OBJECT"
                                        )

                                        .put(
                                                "properties",
                                                new JSONObject()

                                                        .put(
                                                                "order",
                                                                new JSONObject()

                                                                        .put(
                                                                                "type",
                                                                                "STRING"
                                                                        )

                                                                        .put(
                                                                                "description",
                                                                                "Order details"
                                                                        )

                                                        )

                                        )

                        )

        );


        // =================================================
        // GET PRODUCT DETAILS
        // =================================================

        declarations.put(

                new JSONObject()

                        .put(
                                "name",
                                "getProductDetails"
                        )

                        .put(
                                "description",
                                "Retrieve product details"
                        )

                        .put(
                                "parameters",
                                new JSONObject()

                                        .put(
                                                "type",
                                                "OBJECT"
                                        )

                                        .put(
                                                "properties",
                                                new JSONObject()

                                                        .put(
                                                                "product",
                                                                new JSONObject()

                                                                        .put(
                                                                                "type",
                                                                                "STRING"
                                                                        )

                                                                        .put(
                                                                                "description",
                                                                                "Product details"
                                                                        )

                                                        )

                                        )

                        )

        );


        return declarations;

    }


    // =====================================================
    // PROCESS FUNCTION CALL
    // =====================================================

    private FunctionResponse processFunctionCall(

            JSONObject functionCall,

            Long productId,

            Long userId

    ) throws ProductException {


        String functionName =
                functionCall.getString("name");


        FunctionResponse response =
                new FunctionResponse();


        response.setFunctionName(
                functionName
        );


        // =================================================
        // SAVE ORIGINAL FUNCTION CALL
        // =================================================

        response.setOriginalFunctionCall(
                functionCall.toString()
        );


        User user =
                userRepository.findById(userId)
                        .orElse(null);



        switch (functionName) {


            // =================================================
            // CART
            // =================================================

            case "getUserCart":


                Cart cart =
                        cartRepository.findByUserId(userId);


                if (cart == null) {

                    throw new ProductException(
                            "Cart not found"
                    );

                }


                System.out.println(
                        "cart: " + cart.getId()
                );


                response.setUserCart(
                        cart
                );


                break;



            // =================================================
            // ORDERS
            // =================================================

            case "getUsersOrder":


                List<com.yashpal.model.Order> orders =
                        orderRepository.findByUserId(userId);


                OrderHistory orderHistory =
                        OrderMapper.toOrderHistory(
                                orders,
                                user
                        );


                response.setOrderHistory(
                        orderHistory
                );


                break;



            // =================================================
            // PRODUCT
            // =================================================

            case "getProductDetails":


                if (productId == null) {

                    throw new ProductException(
                            "Product ID is required"
                    );

                }


                Product product =
                        productRepository.findById(productId)

                                .orElseThrow(

                                        () -> new ProductException(
                                                "Product not found"
                                        )

                                );


                response.setProduct(
                        product
                );


                break;



            default:


                throw new ProductException(
                        "Unsupported function: " + functionName
                );

        }


        return response;

    }



    // =====================================================
    // FIRST GEMINI REQUEST
    // =====================================================

    public FunctionResponse getFunctionResponse(

            String prompt,

            Long productId,

            Long userId

    ) throws ProductException {


        JSONObject requestBodyJson =

                new JSONObject()

                        .put(
                                "contents",

                                new JSONArray()

                                        .put(

                                                new JSONObject()

                                                        .put(
                                                                "role",
                                                                "user"
                                                        )

                                                        .put(
                                                                "parts",

                                                                new JSONArray()

                                                                        .put(

                                                                                new JSONObject()

                                                                                        .put(
                                                                                                "text",
                                                                                                prompt
                                                                                        )

                                                                        )

                                                        )

                                        )

                        )

                        .put(
                                "tools",

                                new JSONArray()

                                        .put(

                                                new JSONObject()

                                                        .put(
                                                                "functionDeclarations",
                                                                createFunctionDeclarations()
                                                        )

                                        )

                        );



        JSONObject jsonObject =
                callGemini(
                        requestBodyJson
                );


        System.out.println(
                "functionResponse: " + jsonObject
        );



        JSONArray candidates =
                jsonObject.getJSONArray(
                        "candidates"
                );


        JSONObject firstCandidate =
                candidates.getJSONObject(
                        0
                );


        JSONObject content =
                firstCandidate.getJSONObject(
                        "content"
                );


        JSONArray parts =
                content.getJSONArray(
                        "parts"
                );


        JSONObject firstPart =
                parts.getJSONObject(
                        0
                );



        // =================================================
        // FUNCTION CALL RESPONSE
        // =================================================

        if (firstPart.has("functionCall")) {


            JSONObject functionCall =
                    firstPart.getJSONObject(
                            "functionCall"
                    );


            FunctionResponse response =
                    processFunctionCall(

                            functionCall,

                            productId,

                            userId

                    );


            // =================================================
            // IMPORTANT
            // Save complete original function call part
            // INCLUDING thoughtSignature
            // =================================================

            response.setOriginalFunctionCall(
                    firstPart.toString()
            );


            return response;

        }



        // =================================================
        // NORMAL TEXT RESPONSE
        // =================================================

        if (firstPart.has("text")) {


            FunctionResponse response =
                    new FunctionResponse();


            response.setText(
                    firstPart.getString(
                            "text"
                    )
            );


            return response;

        }



        throw new ProductException(
                "Invalid Gemini response"
        );

    }



    // =====================================================
    // GEMINI API CALL
    // =====================================================

    private JSONObject callGemini(

            JSONObject requestBodyJson

    ) {


        HttpHeaders headers =
                new HttpHeaders();


        headers.setContentType(
                MediaType.APPLICATION_JSON
        );


        HttpEntity<String> requestEntity =

                new HttpEntity<>(

                        requestBodyJson.toString(),

                        headers

                );


        RestTemplate restTemplate =
                new RestTemplate();


        ResponseEntity<String> response =

                restTemplate.postForEntity(

                        getGeminiApiUrl(),

                        requestEntity,

                        String.class

                );


        return new JSONObject(
                response.getBody()
        );

    }



    // =====================================================
    // AI CHATBOT
    // =====================================================

    @Override
    public ApiResponse aiChatBot(

            String prompt,

            Long productId,

            Long userId

    ) throws ProductException {


        System.out.println(
                "------- " + prompt
        );


        FunctionResponse functionResponse =

                getFunctionResponse(

                        prompt,

                        productId,

                        userId

                );


        System.out.println(
                "------- " + functionResponse
        );



        // =================================================
        // NORMAL TEXT
        // =================================================

        if (functionResponse.getText() != null) {


            ApiResponse response =
                    new ApiResponse();


            response.setMessage(
                    functionResponse.getText()
            );


            return response;

        }



        // =================================================
        // FUNCTION RESULT JSON
        // =================================================

        JSONObject functionArgs =
                new JSONObject();



        if (functionResponse.getUserCart() != null) {


            functionArgs.put(

                    "cart",

                    functionResponse.getUserCart()

            );

        }



        if (functionResponse.getOrderHistory() != null) {


            functionArgs.put(

                    "order",

                    functionResponse.getOrderHistory()

            );

        }



        if (functionResponse.getProduct() != null) {


            functionArgs.put(

                    "product",

                    ProductMapper.toProductDto(

                            functionResponse.getProduct()

                    )

            );

        }



        // =================================================
        // ORIGINAL MODEL FUNCTION CALL PART
        // =================================================

        JSONObject originalFunctionCallPart =

                new JSONObject(

                        functionResponse.getOriginalFunctionCall()

                );



        // =================================================
        // FUNCTION RESPONSE PART
        // =================================================

        JSONObject functionResponsePart =

                new JSONObject()

                        .put(

                                "functionResponse",

                                new JSONObject()

                                        .put(

                                                "name",

                                                functionResponse.getFunctionName()

                                        )

                                        .put(

                                                "response",

                                                new JSONObject()

                                                        .put(

                                                                "result",

                                                                functionArgs

                                                        )

                                        )

                        );



        // =================================================
        // SECOND GEMINI REQUEST
        // =================================================

        JSONArray contents =

                new JSONArray()



                        // ---------------------------------
                        // USER PROMPT
                        // ---------------------------------

                        .put(

                                new JSONObject()

                                        .put(
                                                "role",
                                                "user"
                                        )

                                        .put(

                                                "parts",

                                                new JSONArray()

                                                        .put(

                                                                new JSONObject()

                                                                        .put(
                                                                                "text",
                                                                                prompt
                                                                        )

                                                        )

                                        )

                        )



                        // ---------------------------------
                        // ORIGINAL MODEL FUNCTION CALL
                        // ---------------------------------

                        .put(

                                new JSONObject()

                                        .put(
                                                "role",
                                                "model"
                                        )

                                        .put(

                                                "parts",

                                                new JSONArray()

                                                        .put(
                                                                originalFunctionCallPart
                                                        )

                                        )

                        )



                        // ---------------------------------
                        // FUNCTION RESULT
                        // ---------------------------------

                        .put(

                                new JSONObject()

                                        .put(
                                                "role",
                                                "user"
                                        )

                                        .put(

                                                "parts",

                                                new JSONArray()

                                                        .put(
                                                                functionResponsePart
                                                        )

                                        )

                        );



        JSONObject body =

                new JSONObject()

                        .put(
                                "contents",
                                contents
                        )

                        .put(

                                "tools",

                                new JSONArray()

                                        .put(

                                                new JSONObject()

                                                        .put(

                                                                "functionDeclarations",

                                                                createFunctionDeclarations()

                                                        )

                                        )

                        );



        System.out.println(
                "Second Gemini Request: " + body
        );



        JSONObject jsonObject =
                callGemini(
                        body
                );



        // =================================================
        // FINAL GEMINI TEXT
        // =================================================

        JSONArray candidates =
                jsonObject.getJSONArray(
                        "candidates"
                );


        JSONObject firstCandidate =
                candidates.getJSONObject(
                        0
                );


        JSONObject content =
                firstCandidate.getJSONObject(
                        "content"
                );


        JSONArray parts =
                content.getJSONArray(
                        "parts"
                );


        String text =
                parts.getJSONObject(
                        0
                ).optString(

                        "text",

                        "I found the information, but could not generate a response."

                );



        ApiResponse response =
                new ApiResponse();


        response.setMessage(
                text
        );


        return response;

    }

}


// package com.yashpal.ai.services;

// import com.yashpal.exception.ProductException;
// import com.yashpal.mapper.OrderMapper;
// import com.yashpal.mapper.ProductMapper;
// import com.yashpal.model.Cart;
// import com.yashpal.model.Order;
// import com.yashpal.model.Product;
// import com.yashpal.model.User;
// import com.yashpal.repository.CartRepository;
// import com.yashpal.repository.OrderRepository;
// import com.yashpal.repository.ProductRepository;
// import com.yashpal.repository.UserRepository;
// import com.yashpal.response.ApiResponse;
// import com.yashpal.response.FunctionResponse;
// import lombok.RequiredArgsConstructor;
// import org.json.JSONArray;
// import org.json.JSONObject;
// import org.springframework.http.HttpEntity;
// import org.springframework.http.HttpHeaders;
// import org.springframework.http.MediaType;
// import org.springframework.http.ResponseEntity;
// import org.springframework.stereotype.Service;
// import org.springframework.web.client.RestTemplate;
// import org.springframework.beans.factory.annotation.Value;
// import java.util.List;
// import com.yashpal.response.FunctionResponse;

// @Service
// @RequiredArgsConstructor
// public class AiChatBotServiceImpl implements AiChatBotService {

//         // String GEMINI_API_KEY = "AIzaSyDp-jeRRqqbr08scpIn1p9rLEL_Nqv5Zuo";

//         private final CartRepository cartRepository;

//         private final OrderRepository orderRepository;

//         private final ProductRepository productRepository;
//         private final UserRepository userRepository;

//         @Value("${gemini.api.key}")
//         private String GEMINI_API_KEY;

//         private JSONArray createFunctionDeclarations() {
//                 return new JSONArray()
//                                 .put(new JSONObject()
//                                                 .put("name", "getUserCart")
//                                                 .put("description", "Retrieve the user's cart details")
//                                                 .put("parameters", new JSONObject()
//                                                                 .put("type", "OBJECT")
//                                                                 .put("properties", new JSONObject()
//                                                                                 .put("cart", new JSONObject()
//                                                                                                 .put("type", "STRING")
//                                                                                                 .put("description",
//                                                                                                                 "Cart Details, like total item in cart, cart item, remove item from cart, cart Id")))
//                                                                 .put("required", new JSONArray()
//                                                                                 .put("cart"))))
//                                 .put(new JSONObject()
//                                                 .put("name", "getUsersOrder")
//                                                 .put("description", "Retrieve the user's order details")
//                                                 .put("parameters", new JSONObject()
//                                                                 .put("type", "OBJECT")
//                                                                 .put("properties", new JSONObject()
//                                                                                 .put("order", new JSONObject()
//                                                                                                 .put("type", "STRING")
//                                                                                                 .put("description",
//                                                                                                                 "Order Details, order, total order, current order, delivered order, pending order, current order, cancled order")))
//                                                                 .put("required", new JSONArray()
//                                                                                 .put("order"))))
//                                 .put(new JSONObject()
//                                                 .put("name", "getProductDetails")
//                                                 .put("description", "Retrieve product details")
//                                                 .put("parameters", new JSONObject()
//                                                                 .put("type", "OBJECT")
//                                                                 .put("properties", new JSONObject()
//                                                                                 .put("product", new JSONObject()
//                                                                                                 .put("type", "STRING")
//                                                                                                 .put("description",
//                                                                                                                 "The Product Details like, Product title, product id, product color, product size, selling price, mrp price, rating extra...")))
//                                                                 .put("required", new JSONArray()
//                                                                                 .put("product"))));
//         }

//         private FunctionResponse processFunctionCall(JSONObject functionCall,
//                         Long productId,
//                         Long userId) throws ProductException {
//                 String functionName = functionCall.getString("name");
//                 JSONObject args = functionCall.getJSONObject("args");

//                 FunctionResponse res = new FunctionResponse();
//                 res.setFunctionName(functionName);
//                 User user = userRepository.findById(userId).orElse(null);

//                 switch (functionName) {
//                         case "getUserCart":
//                                 // Long userId = Long.parseLong(args.getString("userId"));
//                                 Cart cart = cartRepository.findByUserId(userId);
//                                 System.out.println("cart: " + cart.getId());
//                                 res.setUserCart(cart);
//                                 break;
//                         case "getUsersOrder":
//                                 // Long orderId = Long.parseLong(args.getString("orderId"));
//                                 List<Order> orders = orderRepository.findByUserId(userId);
//                                 res.setOrderHistory(OrderMapper.toOrderHistory(orders, user));
//                                 System.out.println("order history: " + OrderMapper.toOrderHistory(orders, user));
//                                 break;
//                         case "getProductDetails":
//                                 // Long productId = Long.parseLong(args.getString("productId"));
//                                 Product product = productRepository.findById(productId).orElseThrow(
//                                                 () -> new ProductException("product not found"));

//                                 res.setProduct(product);
//                                 break;
//                         default:
//                                 throw new IllegalArgumentException("Unsupported function: " + functionName);
//                 }
//                 return res;
//         }

//         public FunctionResponse getFunctionResponse(String prompt, Long productId, Long userId)
//                         throws ProductException {
//                 String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.6-flash:generateContent?key="
//                                 + GEMINI_API_KEY;
//                 JSONObject requestBodyJson = new JSONObject()
//                                 .put("contents", new JSONArray()
//                                                 .put(new JSONObject()
//                                                                 .put("parts", new JSONArray()
//                                                                                 .put(new JSONObject()
//                                                                                                 .put("text", prompt)))))
//                                 .put("tools", new JSONArray()
//                                                 .put(new JSONObject()
//                                                                 .put("functionDeclarations",
//                                                                                 createFunctionDeclarations())));

//                 HttpHeaders headers = new HttpHeaders();
//                 headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);

//                 HttpEntity<String> requestEntity = new HttpEntity<>(requestBodyJson.toString(), headers);

//                 RestTemplate restTemplate = new RestTemplate();
//                 ResponseEntity<String> response = restTemplate.postForEntity(GEMINI_API_URL, requestEntity,
//                                 String.class);

//                 String responseBody = response.getBody();
//                 JSONObject jsonObject = new JSONObject(responseBody);

//                 System.out.println("functionResponse: " + responseBody);
//                 JSONArray candidates = jsonObject.getJSONArray("candidates");
//                 JSONObject firstCandidate = candidates.getJSONObject(0);
//                 JSONObject content = firstCandidate.getJSONObject("content");
//                 JSONArray parts = content.getJSONArray("parts");
//                 JSONObject firstPart = parts.getJSONObject(0);
//                 // JSONObject functionCall = firstPart.getJSONObject("functionCall");

//                 // return processFunctionCall(functionCall, productId, userId);
//                 // Check whether Gemini returned a function call
//                 if (firstPart.has("functionCall")) {

//                         JSONObject functionCall = firstPart.getJSONObject("functionCall");

//                         return processFunctionCall(
//                                         functionCall,
//                                         productId,
//                                         userId);

//                 }

//                 // Normal text response
//                 if (firstPart.has("text")) {

//                         String text = firstPart.getString("text");

//                         FunctionResponse response1 = new FunctionResponse();

//                         response1.setText(text);

//                         return response1;
//                 }

//                 // No valid response
//                 throw new ProductException("Invalid Gemini response");
//         }

//         @Override
//         public ApiResponse aiChatBot(String prompt, Long productId, Long userId) throws ProductException {
//                 String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.6-flash:generateContent?key="
//                                 + GEMINI_API_KEY;

//                 System.out.println("------- " + prompt);

//                 FunctionResponse functionResponse = getFunctionResponse(prompt, productId, userId);
//                 System.out.println("------- " + functionResponse);

//                 // If Gemini returned normal text
//                 if (functionResponse.getText() != null) {

//                         ApiResponse res = new ApiResponse();

//                         res.setMessage(functionResponse.getText());

//                         return res;
//                 }

//                 HttpHeaders headers = new HttpHeaders();
//                 headers.setContentType(MediaType.APPLICATION_JSON);

//                 // Construct the request body
//                 String body = new JSONObject()
//                                 .put("contents", new JSONArray()
//                                                 .put(new JSONObject()
//                                                                 .put("role", "user")
//                                                                 .put("parts", new JSONArray()
//                                                                                 .put(new JSONObject()
//                                                                                                 .put("text", prompt))))
//                                                 .put(new JSONObject()
//                                                                 .put("role", "model")
//                                                                 .put("parts", new JSONArray()
//                                                                                 .put(new JSONObject()
//                                                                                                 .put("functionCall",
//                                                                                                                 new JSONObject()
//                                                                                                                                 .put("name", functionResponse
//                                                                                                                                                 .getFunctionName())
//                                                                                                                                 .put("args", new JSONObject()
//                                                                                                                                                 .put("cart", functionResponse
//                                                                                                                                                                 .getUserCart() != null
//                                                                                                                                                                                 ? functionResponse
//                                                                                                                                                                                                 .getUserCart()
//                                                                                                                                                                                                 .getUser()
//                                                                                                                                                                                 : null)
//                                                                                                                                                 .put("order", functionResponse
//                                                                                                                                                                 .getOrderHistory() != null
//                                                                                                                                                                                 ? functionResponse
//                                                                                                                                                                                                 .getOrderHistory()
//                                                                                                                                                                                 : null)
//                                                                                                                                                 .put("product", functionResponse
//                                                                                                                                                                 .getProduct() != null
//                                                                                                                                                                                 ? ProductMapper.toProductDto(
//                                                                                                                                                                                                 functionResponse.getProduct())
//                                                                                                                                                                                 : null))))))
//                                                 .put(new JSONObject()
//                                                                 .put("role", "function")
//                                                                 .put("parts", new JSONArray()
//                                                                                 .put(new JSONObject()
//                                                                                                 .put("functionResponse",
//                                                                                                                 new JSONObject()
//                                                                                                                                 .put("name", functionResponse
//                                                                                                                                                 .getFunctionName())
//                                                                                                                                 .put("response", new JSONObject()
//                                                                                                                                                 .put("name", functionResponse
//                                                                                                                                                                 .getFunctionName())
//                                                                                                                                                 .put("content", functionResponse)))))))
//                                 .put("tools", new JSONArray()
//                                                 .put(new JSONObject()
//                                                                 .put("functionDeclarations",
//                                                                                 createFunctionDeclarations())))
//                                 .toString();

//                 // Make the API request
//                 HttpEntity<String> request = new HttpEntity<>(body, headers);
//                 RestTemplate restTemplate = new RestTemplate();
//                 ResponseEntity<String> response = restTemplate.postForEntity(GEMINI_API_URL, request, String.class);

//                 // Process the response
//                 String responseBody = response.getBody();
//                 JSONObject jsonObject = new JSONObject(responseBody);

//                 // Extract the first candidate
//                 JSONArray candidates = jsonObject.getJSONArray("candidates");
//                 JSONObject firstCandidate = candidates.getJSONObject(0);

//                 // Extract the text
//                 JSONObject content = firstCandidate.getJSONObject("content");
//                 JSONArray parts = content.getJSONArray("parts");
//                 JSONObject firstPart = parts.getJSONObject(0);
//                 String text = firstPart.getString("text");

//                 // Prepare and return the API response
//                 ApiResponse res = new ApiResponse();
//                 res.setMessage(text);
//                 return res;

//         }
// }
